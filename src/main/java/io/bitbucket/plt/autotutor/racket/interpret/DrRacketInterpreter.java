package io.bitbucket.plt.autotutor.racket.interpret;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import io.bitbucket.plt.autotutor.DrRacketLexer;
import io.bitbucket.plt.autotutor.DrRacketParser;
import io.bitbucket.plt.autotutor.racket.functions.CustomFunction;
import io.bitbucket.plt.autotutor.racket.functions.numbers.*;
import io.bitbucket.plt.autotutor.racket.test.*;
import io.bitbucket.plt.autotutor.racket.test.Number;
import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.saxon.Configuration;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmNode;

public class DrRacketInterpreter {

	private static final String DEFAULT_XQUERY_FILE = "interpret.xqy";
	private boolean parseErrorsOccurred;
	private String errorOutput;
	private String xml;
	private String rktFile;
	//Provisorisch
	private Expression expression;
	private List<Expression> expressionList;	//alle expressions, die gegeben wurden

	public DrRacketInterpreter(String rktFile) throws Exception {

		this.rktFile = rktFile;

		DrRacketLexer lexer = new DrRacketLexer(CharStreams.fromString(rktFile));

		DrRacketParser parser = new DrRacketParser(new CommonTokenStream(lexer));

		ANTLRErrorStrategy errorStrategy = new DefaultErrorStrategy() {
			@Override
			public void reportError(Parser recognizer, RecognitionException e) {
				super.reportError(recognizer, e);
				parseErrorsOccurred = true;
			}

		};
		parser.setErrorHandler(errorStrategy);
		PrintStream origSysErr = System.err;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try (PrintStream ps = new PrintStream(bos)) {
			System.setErr(ps);
			parser.start();
			System.setErr(origSysErr);
		}

		errorOutput = bos.toString();
		if (parseErrorsOccurred) {
			throw new Exception("Ein Fehler ist beim Einlesen der DrRacket-Datei aufgetreten. "
					+ "Vermutlich ist die Datei nicht im Text-Format gespeichert. "
					+ "Das passiert z.B. dann, wenn Bilder Teil des Programms sind. "
					+ "Probieren Sie die Datei über 'File' -> 'Save Other' -> 'Save Definitions as Text ...' zu speichern. "
					+ "Bilder gehen dabei verloren und Berechnungen, die von den Bildern abhängen werden dadurch vorraussichtlich fehlerhaft.");
		}

		// pretty printing
		xml = prettyPrint(parser.xml.toString());

		//Code Updates

		System.out.println("IM CODE --------------------------");

		Iterator iter = Arrays.stream(xml.split("\n")).iterator(); //Itterieren über die Eingabe

		Expression ex = new Expression();
		Expression exacc = ex;	//exacc = expression aktuell
		Stack<Expression> stack = new Stack<>();	//Hilft dabei die richtige Strucktur beizubehalten, bei Verschachtelungen
		Expression temp = new Expression();
		exacc.addPart(temp);
		stack.push(exacc);
		exacc = temp;


		List<CustomFunction> customFunctionList = new LinkedList<>();
		boolean customFunctionListInitialisation = false;
		boolean head = false;
		boolean funName = false;
		String tempFunName = null;
		List<Parameter> tempParameterList = new LinkedList<>();
		Expression tempBody = new Expression();
		Expression exaccRem = null;	//exacc = expression aktuell Remember

		while (iter.hasNext()) {	//Itterieren über die Eingabe
			String now = (String) iter.next();	//now = Aktueller String (Zeile des Eingabecodes)
			if (now.contains("<paren") && now.contains("type=\"round\">")) {	//Verscchachtelung
				if (customFunctionListInitialisation) {
					exaccRem = exacc;
					exacc = tempBody;
				} else {
					if(!exacc.getParts().isEmpty()) {
						temp = new Expression();
						exacc.addPart(temp);
						stack.push(exacc);
						exacc = temp;
					}

				}
				//continue;
			}

			if (now.contains("type=\"Name\"") && customFunctionListInitialisation && !head) {	//Temp Body
				//exaccRem = exacc;
				//exacc = tempBody;
				//continue;
			}

			if (now.contains("type=\"Name\"") && now.contains("value=\"+\"")) {	//Plus
				exacc.addPart(new Plus());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"-\"")) {	//Minus
				exacc.addPart(new Minus());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"*\"")) {	//Multiplication
				exacc.addPart(new Multiplication());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"/\"")) {	//Multiplication
				exacc.addPart(new Division());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"&lt;\"")) {	//Less Than
				exacc.addPart(new LessThan());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"&gt;\"")) {	//Greater Than
				exacc.addPart(new GreaterThan());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"=\"")) {	//Equal
				exacc.addPart(new Equal());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"&lt;=\"")) {	//Lesser Or Equal Than
				exacc.addPart(new LessOrEqualThan());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"&gt;=\"")) {	//Greater Or Equal Than
				exacc.addPart(new GreaterOrEqualThan());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"abs\"")) {	//Absolute
				exacc.addPart(new Absolute());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"add1\"")) {	//Add1
				exacc.addPart(new Add1());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"ceiling\"")) {	//Ceiling
				exacc.addPart(new Ceiling());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"even?\"")) {	//Even
				exacc.addPart(new Even());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"exp\"")) {	//Exp
				exacc.addPart(new Exp());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"floor\"")) {	//Floor
				exacc.addPart(new Floor());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"log\"")) {	//Log
				exacc.addPart(new Log());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"max\"")) {	//Max
				exacc.addPart(new Max());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"min\"")) {	//Min
				exacc.addPart(new Min());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"modulo\"")) {	//Modulo
				exacc.addPart(new Modulo());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"negative?\"")) {	//Negative
				exacc.addPart(new Negative());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"odd?\"")) {	//Odd
				exacc.addPart(new Odd());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"positive?\"")) {	//Positive
				exacc.addPart(new Positive());
				continue;
			}

			//Custome Funktion
			if (now.contains("type=\"Name\"") && customFunctionListInitialisation && !head) {	//Parameter
				String s = now.split("value=\"")[1];
				exacc.addPart(new Parameter(s.substring(0,s.length()-3)));

			}

			if (now.contains("type=\"Name\"") && head && funName) {	//Temp parameter List
				String s = now.split("value=\"")[1];
				tempParameterList.add(new Parameter(s.substring(0,s.length()-3)));

			}
			if (now.contains("type=\"Name\"") && head && !funName) {	//Temp Fuction Name
				String s = now.split("value=\"")[1];
				tempFunName = s.substring(0,s.length()-3);
				funName = true;

			}

			if (now.contains("type=\"Name\"") && !customFunctionListInitialisation) {	//custome fun einsetzten
				String s = now.split("value=\"")[1];
				if(customFunctionList.stream().
						map(x -> x.getFunName()).
						anyMatch(x -> x.equals(s.substring(0,s.length()-3)))) {	//Funktion ist vorhanden

					exacc.addPart(customFunctionList.stream().
							filter(x -> x.getFunName().equals(s.substring(0,s.length()-3))).
							reduce( (x, y) -> x).orElse(null)) ;
				} else {
					//System.out.println("FUNKTION IST NICHT VORHANDEN! ");
				}

			}


			if (now.contains("type=\"Name\"") && now.contains("value=\"define\"")) {
				customFunctionListInitialisation = true;
				head = true;

			}




			if(now.contains("type=\"Number\"")) {	//float
				Pattern pattern = Pattern.compile("\"[^\"]*\"");
				Matcher matcher = pattern.matcher(now);

				String[] str = matcher.results().map(x -> x.group()).map(x -> x.replaceAll("\"", "")).toArray(String[]::new);
				//Deswegen 2, weil 0 die line ist ist und 1 der typ
				exacc.addPart(new Number(Float.valueOf(str[2])));

			}
			if (now.contains("</paren>")) {	//Entschachtelung
				if (customFunctionListInitialisation) {
					if (head) {
						head = false;
						funName = false;
						//System.out.println(tempFunName);
						//System.out.println(tempParameterList);
					} else {	//Body
						customFunctionListInitialisation = false;
						//System.out.println(tempBody);

						//exaccRem = exacc;
						exacc = temp;
						customFunctionList.add(new CustomFunction(tempFunName, tempParameterList, tempBody));

						tempFunName = null;
						tempParameterList = new LinkedList<>();
						tempBody = new Expression();

						//System.out.println(customFunctionList.get(0));
					}
				} else {
					if (!exacc.getParts().isEmpty())
						exacc = stack.pop();
				}

			}
		}
		expressionList = ex.getParts();
		System.out.println(ex);
		System.out.println("---");
		expression = ex;
		//System.out.println(ex.evaluate(new Expression())); TODO must be fixed
		System.out.println("---");
		expressionList.forEach(x -> System.out.println(x));
		//expressionList.stream().map(x -> x.evaluate(new Expression())).forEach(x -> System.out.println(x)); TODO must be fixed

		System.out.println("CODE ENDE--------------------------");

	}

	public String evaluateExpressions() {
		return expression.evaluate(new Expression());
	}

	public List<String> getAllExpressionEvaluations() {
		return expressionList.stream().map(x -> x.evaluate(new Expression())).collect(Collectors.toList());
	}

	private String prettyPrint(String xml) throws SAXException, IOException, ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		InputSource src = new InputSource(new StringReader(xml.toString()));
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		Writer out = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(out));
		return out.toString();
	}

	/**
	 * Interpret the Racket program with the XQuery expression from the default file.
	 * @return
	 * @throws Exception
	 */
	public String interpretWithXQuery() throws Exception {
		// read the XQuery expression from a file on the class path (e.g., the src/main/resources folder)
		String query = IOUtils.toString(ClassLoader.getSystemResourceAsStream(DEFAULT_XQUERY_FILE), Charset.defaultCharset());

		return interpretWithXQuery(query);
	}

	/**
	 * Interpret the Racket program with the passed XQuery expression.
	 * @return
	 * @throws Exception
	 */
	public String interpretWithXQuery(String query) throws SaxonApiException, Exception, IOException {
		// prepare query execution with Saxon:
		Processor processor = new Processor(Configuration.newConfiguration());

		// create parsed XML document
		InputSource is = new InputSource(new StringReader(xml));
		DocumentBuilder builder = processor.newDocumentBuilder();
		XdmNode doc = builder.build(new SAXSource(is));

		
		XQueryCompiler compiler = processor.newXQueryCompiler();

		// prepare XQuery evaluation		
		XQueryExecutable exp = compiler.compile(query);
		final XQueryEvaluator evaluator = exp.load();
		evaluator.setContextItem(doc);

		// check if XQuery expression is correct
		if (!exp.getUnderlyingCompiledQuery().usesContextItem()) {
			throw new Exception("Fehlerhafter Check (XQuery verwendet Context-Item nicht).");
		}

		// prepare serializer for result of XQuery expreession
		// the result will be written to bos
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Serializer serializer = processor.newSerializer(bos);
		serializer.setOutputProperty(Serializer.Property.METHOD, "adaptive");
	
		// execute query
		evaluator.run(serializer);
		
		// collect result
		bos.flush();
		String result = bos.toString();
		bos.close();
		
		// done
		return result;
	}

	public String getInput() {
		return rktFile;
	}

	public String getXml() {
		return xml;
	}

	public boolean hasParseError() {
		return parseErrorsOccurred;
	}
	
	public String getParseErrors() {
		if (parseErrorsOccurred)
			return errorOutput;
		else
			return "No parse errors.";	
	}
}
