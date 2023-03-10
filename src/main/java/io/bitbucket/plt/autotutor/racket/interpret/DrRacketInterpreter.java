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
import io.bitbucket.plt.autotutor.racket.functions.booleans.BooleanEQ;
import io.bitbucket.plt.autotutor.racket.functions.booleans.BooleanQ;
import io.bitbucket.plt.autotutor.racket.functions.booleans.FalseQ;
import io.bitbucket.plt.autotutor.racket.functions.booleans.Not;
import io.bitbucket.plt.autotutor.racket.functions.numbers.*;
import io.bitbucket.plt.autotutor.racket.functions.numbers.Random;
import io.bitbucket.plt.autotutor.racket.test.*;
import io.bitbucket.plt.autotutor.racket.test.Boolean;
import io.bitbucket.plt.autotutor.racket.test.Number;
import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.*;
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
	private List<CustomFunction> customFunctionList = new LinkedList<>();

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

		//System.out.println("IM CODE --------------------------");





/*
		Iterator iter = Arrays.stream(xml.split("\n")).iterator(); //Itterieren über die Eingabe

		Expression ex = new Expression();
		Expression exacc = ex;	//exacc = expression aktuell
		Stack<Expression> stack = new Stack<>();	//Hilft dabei die richtige Strucktur beizubehalten, bei Verschachtelungen
		Expression temp = new Expression();
		exacc.addPart(temp);
		stack.push(exacc);
		exacc = temp;



		boolean customFunctionListInitialisation = false;
		boolean head = false;
		boolean funName = false;
		String tempFunName = null;
		List<Parameter> tempParameterList = new LinkedList<>();
		Expression tempBody = new Expression();
		Expression exaccRem = null;	//exacc = expression aktuell Remember
/*
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
			if (now.contains("type=\"Name\"") && now.contains("value=\"/\"")) {	//Division
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
			if (now.contains("type=\"Name\"") && now.contains("value=\"random\"")) {	//Random
				exacc.addPart(new Random());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"round\"")) {	//Round
				exacc.addPart(new Round());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"sqr\"")) {	//Sqr
				exacc.addPart(new Sqr());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"sqrt\"")) {	//Sqrt
				exacc.addPart(new Sqrt());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"sub1\"")) {	//Sqrt
				exacc.addPart(new Sub1());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"zero?\"")) {	//Zero?
				exacc.addPart(new Zero());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"boolean=?\"")) {	//Boolean=?
				exacc.addPart(new BooleanEQ());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"boolean?\"")) {	//Boolean?
				exacc.addPart(new BooleanQ());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"false?\"")) {	//False?
				exacc.addPart(new FalseQ());
				continue;
			}
			if (now.contains("type=\"Name\"") && now.contains("value=\"not\"")) {	//Not
				exacc.addPart(new Not());
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


			if(now.contains("type=\"Boolean\"")) {	//Boolean
				Pattern pattern = Pattern.compile("\"[^\"]*\"");
				Matcher matcher = pattern.matcher(now);

				String[] str = matcher.results().map(x -> x.group()).map(x -> x.replaceAll("\"", "")).toArray(String[]::new);
				//Deswegen 2, weil 0 die line ist ist und 1 der typ
				exacc.addPart(new Boolean(java.lang.Boolean.valueOf(str[2])));

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
		//System.out.println(ex.evaluate(new Expression())); ALT
		System.out.println("---");
		expressionList.forEach(x -> System.out.println(x));
		//expressionList.stream().map(x -> x.evaluate(new Expression())).forEach(x -> System.out.println(x)); ALT
*/
		System.out.println("CODE ENDE--------------------------");

 */

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

	private static void removeText (NodeList children) {
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals("#text")) {
				children.item(i).getParentNode().removeChild(children.item(i));
			}
		}
	}

	private Expression goDeeper(NodeList nodeList) {
		Expression expression = new Expression();

		for (int j = 0; j < nodeList.getLength(); j++) {
			NamedNodeMap inside = nodeList.item(j).getAttributes();
			String typeString = inside.getNamedItem("type").toString();
			if (typeString.compareTo("type=\"round\"") == 0) {	//TODO andere Klammern?
				removeText(nodeList.item(j).getChildNodes());

				expression.addPart(goDeeper(nodeList.item(j).getChildNodes()));
				continue;
			}

			System.out.println("\tname is : " + nodeList.item(j).getNodeName() + "( " + inside.getNamedItem("type") + " | " + inside.getNamedItem("value") + " )");
			String valueString = inside.getNamedItem("value").toString();



			//On Custom function
			if (valueString.compareTo("value=\"define\"") == 0) {	//Define
				customFunction(nodeList.item(j).getParentNode(), j);
				System.out.println("Finished Custom Function");
				j += 2;
				continue;
			}

			if (typeString.compareTo("type=\"Name\"") == 0) {
				if (!typeName(valueString, expression)) {	//Prüfen, ob es einen Typen gibt
					if (customFunctionList.stream().map(x -> x.getFunName()).noneMatch(x -> x != valueString)) {	//Ist es keine Custom Function, dann ist es ein Parameter
						expression.addPart(new Parameter(valueString.substring(7, valueString.length() - 1)));
					} else { //Ansosnten customFunction
						System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
						System.out.println(valueString.substring(7,valueString.length()-1));
						customFunctionList.stream().forEach(x -> System.out.println(x.getFunName()));
						System.out.println(customFunctionList.stream().filter(x -> x.getFunName().compareTo(valueString.substring(7,valueString.length()-1)) == 0).findFirst().orElse(null));
						expression.addPart(customFunctionList.stream().filter(x -> x.getFunName() != valueString).findFirst().orElse(null));
					}

				}

			}
			if (typeString.compareTo("type=\"Number\"") == 0) {
				expression.addPart(new Number(Float.valueOf(valueString.substring(7,valueString.length()-1))));
			}
			if (typeString.compareTo("type=\"Boolean\"") == 0) {
				expression.addPart(new Boolean(java.lang.Boolean.valueOf(valueString.substring(7,valueString.length()-1))));
			}

		}

		return expression;
	}

	private static boolean typeName(String valueString, Expression expression) {
		//On Numbers
		if (valueString.compareTo("value=\"+\"") == 0) {
			expression.addPart(new Plus());
			return true;
		}
		if (valueString.compareTo("value=\"-\"") == 0) {
			expression.addPart(new Minus());
			return true;
		}
		if (valueString.compareTo("value=\"*\"") == 0) {
			expression.addPart(new Multiplication());
			return true;
		}
		if (valueString.compareTo("value=\"/\"") == 0) {
			expression.addPart(new Division());
			return true;
		}
		if (valueString.compareTo("value=\"<\"") == 0) {	//Less Than
			expression.addPart(new LessThan());
			return true;
		}
		if (valueString.compareTo("value=\">\"") == 0) {	//Greater Than
			expression.addPart(new GreaterThan());
			return true;
		}
		if (valueString.compareTo("value=\"=\"") == 0) {	//Equal
			expression.addPart(new Equal());
			return true;
		}
		if (valueString.compareTo("value=\"<=\"") == 0) {	//Lesser Or Equal Than
			expression.addPart(new LessOrEqualThan());
			return true;
		}
		if (valueString.compareTo("value=\">=\"") == 0) {	//Greater Or Equal Than
			expression.addPart(new GreaterOrEqualThan());
			return true;
		}
		if (valueString.compareTo("value=\"abs\"") == 0) {	//Absolute
			expression.addPart(new Absolute());
			return true;
		}
		if (valueString.compareTo("value=\"add1\"") == 0) {	//Add1
			expression.addPart(new Add1());
			return true;
		}
		if (valueString.compareTo("value=\"ceiling\"") == 0) {	//Ceiling
			expression.addPart(new Ceiling());
			return true;
		}
		if (valueString.compareTo("value=\"even?\"") == 0) {	//Even
			expression.addPart(new Even());
			return true;
		}
		if (valueString.compareTo("value=\"exp\"") == 0) {	//Exp
			expression.addPart(new Exp());
			return true;
		}
		if (valueString.compareTo("value=\"floor\"") == 0) {	//Floor
			expression.addPart(new Floor());
			return true;
		}
		if (valueString.compareTo("value=\"log\"") == 0) {	//Log
			expression.addPart(new Log());
			return true;
		}
		if (valueString.compareTo("value=\"max\"") == 0) {	//Max
			expression.addPart(new Max());
			return true;
		}
		if (valueString.compareTo("value=\"min\"") == 0) {	//Min
			expression.addPart(new Min());
			return true;
		}
		if (valueString.compareTo("value=\"modulo\"") == 0) {	//Modulo
			expression.addPart(new Modulo());
			return true;
		}
		if (valueString.compareTo("value=\"negative?\"") == 0) {	//Negative
			expression.addPart(new Negative());
			return true;
		}
		if (valueString.compareTo("value=\"odd?\"") == 0) {	//Odd
			expression.addPart(new Odd());
			return true;
		}
		if (valueString.compareTo("value=\"positive?\"") == 0) {	//Positive
			expression.addPart(new Positive());
			return true;
		}
		if (valueString.compareTo("value=\"random\"") == 0) {	//Random
			expression.addPart(new Random());
			return true;
		}
		if (valueString.compareTo("value=\"round\"") == 0) {	//Round
			expression.addPart(new Round());
			return true;
		}
		if (valueString.compareTo("value=\"sqr\"") == 0) {	//Sqr
			expression.addPart(new Sqr());
			return true;
		}
		if (valueString.compareTo("value=\"sqrt\"") == 0) {	//Sqrt
			expression.addPart(new Sqrt());
			return true;
		}
		if (valueString.compareTo("value=\"sub1\"") == 0) {	//Sqrt
			expression.addPart(new Sub1());
			return true;
		}
		if (valueString.compareTo("value=\"zero?\"") == 0) {	//Zero?
			expression.addPart(new Zero());
			return true;
		}
		//On Boolean
		if (valueString.compareTo("value=\"boolean=?\"") == 0) {	//Boolean=?
			expression.addPart(new BooleanEQ());
			return true;
		}
		if (valueString.compareTo("value=\"boolean?\"") == 0) {	//Boolean?
			expression.addPart(new BooleanQ());
			return true;
		}
		if (valueString.compareTo("value=\"false?\"") == 0) {	//False?
			expression.addPart(new FalseQ());
			return true;
		}
		if (valueString.compareTo("value=\"not\"") == 0) {	//Not
			expression.addPart(new Not());
			return true;
		}
		return false;
	}

	private Expression customFunction(Node node, int number) {
		System.out.println("Start Custom Function");
		Expression expression = new Expression();
		String funName = "";
		List<Parameter> parameterList = new LinkedList<>();
		Expression body = new Expression();

		NodeList nodeList = node.getChildNodes();
		removeText(nodeList);

		NodeList headerNodeChildren = nodeList.item(number+1).getChildNodes();
		removeText(headerNodeChildren);
		NodeList bodyNodeChildren = nodeList.item(number+2).getChildNodes();
		removeText(bodyNodeChildren);


		System.out.println("Headder: ");

		for (int i = 0; i < headerNodeChildren.getLength(); i++) {
			NamedNodeMap inside = headerNodeChildren.item(i).getAttributes();
			String valueString = inside.getNamedItem("value").toString();
			String typeString = inside.getNamedItem("type").toString();
			System.out.println("\tname is : " + headerNodeChildren.item(i).getNodeName() + "( " + typeString + " | " + valueString + " )");
			if (funName == "") {
				funName = valueString.substring(7,valueString.length()-1);
			} else {
				parameterList.add(new Parameter(valueString.substring(7,valueString.length()-1)));
			}
		}

		System.out.println("Body: ");

		body = goDeeper(bodyNodeChildren);




		System.out.println("FunName: " + funName);
		System.out.println("ParameterList: " + parameterList);
		System.out.println("Body: " + body);
		CustomFunction customFunction = new CustomFunction(funName, parameterList, body);
		this.customFunctionList.add(customFunction);
		return customFunction;
	}

	public void master() throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(xml)));

		Element root = document.getDocumentElement();
		NodeList children = root.getChildNodes();

		removeText(children);
		Expression rootExpression = new Expression();

		for (int i = 0; i < children.getLength(); i++) {
			System.out.println("name is : " + children.item(i).getNodeName());
			NodeList c2 = children.item(i).getChildNodes();
			removeText(c2);
			if (!goDeeper(c2).getParts().isEmpty())
				rootExpression.addPart(goDeeper(c2));
		}
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println(rootExpression);
		//System.out.println(rootExpression.evaluate(new Expression()));
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		expression = rootExpression;
		expressionList = rootExpression.getParts();
	}
}
