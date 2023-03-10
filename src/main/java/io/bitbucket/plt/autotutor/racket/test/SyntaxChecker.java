package io.bitbucket.plt.autotutor.racket.test;

import io.bitbucket.plt.autotutor.racket.interpret.DrRacketInterpreter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SyntaxChecker {

    /** Bisher nur Funktionen mit Number als Parameter */
    String[] knownFunctions = new String[]{
                                        // On Numbers
                                        "+", "-", "*", "/", "<", "<=", "=", ">", ">=", "abs", "add1", "ceiling", "even?", "exp",
                                        "expt", "floor", "log", "max", "min", "modulo", "negative?", "odd?", "positive?", "random",
                                        "round", "sqr", "sqrt", "sub1", "zero?",
                                        // on Booleans
                                        "boolean=?", "boolean?", "false?", "not",
                                        // on Lists
                                        "append", "cons", "empty?", "first", "length", "list", "member", "range", "remove", "second",
                                        // on Posns
                                        "make-posn", "posn-x", "posn-y"};

    /** Alle definierten Variabeln, sowohl vordefiniert, als auch selber definierte */
    String[] knownVariables = new String[]{"empty", "pi", "null", "e"};
    List<String> knownFunctionsList;
    List<String> knownVariablesList;

    /** Jedem Funktionsnamen wird ein Array zugeordnet mit Angaben zu den Parametern und dem Rückgabetyp.
     *  Das Array hat die Form {Rückgabetyp, Parametertyp_1, ..., Parametertyp_n, (INFINITE)}.
     *  INFINITE nur dann, wenn der vorherige Parametertyp unendlich oft vorkommen kann.
     */
    HashMap<String, String[]> parametersOfFunction;
    HashMap<String, String> parameterOfVariable;

    /**
     * Creates the Parameter-Arrays for every Function and assigns them to the function name
     */
    public SyntaxChecker() {
        knownFunctionsList = Arrays.asList(knownFunctions);
        knownVariablesList = Arrays.asList(knownVariables);
        parametersOfFunction = new HashMap<>();
        parameterOfVariable = new HashMap<>();

        // On numbers
        String[] parameterNumberNumberNumberInfinite = {"Number", "Number", "Number", "INFINITE"};
        parametersOfFunction.put("+", parameterNumberNumberNumberInfinite);
        parametersOfFunction.put("*", parameterNumberNumberNumberInfinite);
        parametersOfFunction.put("/", parameterNumberNumberNumberInfinite);

        String[] parameterBooleanNumberNumberInfinite = {"Boolean", "Number", "Number", "INFINITE"};
        parametersOfFunction.put("<", parameterBooleanNumberNumberInfinite);
        parametersOfFunction.put("<=", parameterBooleanNumberNumberInfinite);
        parametersOfFunction.put(">", parameterBooleanNumberNumberInfinite);
        parametersOfFunction.put(">=", parameterBooleanNumberNumberInfinite);
        parametersOfFunction.put("=", parameterBooleanNumberNumberInfinite);

        String[] parameterNumberNumberInfinite = {"Number", "Number", "INFINITE"};
        parametersOfFunction.put("max", parameterNumberNumberInfinite);
        parametersOfFunction.put("min", parameterNumberNumberInfinite);
        parametersOfFunction.put("-", parameterNumberNumberInfinite);

        String[] parameterNumberNumber = {"Number", "Number"};
        parametersOfFunction.put("abs", parameterNumberNumber);
        parametersOfFunction.put("add1", parameterNumberNumber);
        parametersOfFunction.put("ceiling", parameterNumberNumber);
        parametersOfFunction.put("floor", parameterNumberNumber);
        parametersOfFunction.put("random", parameterNumberNumber);
        parametersOfFunction.put("round", parameterNumberNumber);
        parametersOfFunction.put("sqr", parameterNumberNumber);
        parametersOfFunction.put("sqrt", parameterNumberNumber);
        parametersOfFunction.put("sub1", parameterNumberNumber);

        String[] parameterBooleanNumber = {"Boolean", "Number"};
        parametersOfFunction.put("even?", parameterBooleanNumber);
        parametersOfFunction.put("odd?", parameterBooleanNumber);
        parametersOfFunction.put("negative?", parameterBooleanNumber);
        parametersOfFunction.put("positive?", parameterBooleanNumber);
        parametersOfFunction.put("zero?", parameterBooleanNumber);

        String[] parameterHashnameNumber = {"HashName", "Number"};
        parametersOfFunction.put("exp", parameterHashnameNumber);
        parametersOfFunction.put("log", parameterHashnameNumber);


        String[] parameterNumberNumberNumber = {"Number", "Number", "Number"};
        parametersOfFunction.put("expt", parameterNumberNumberNumber);
        parametersOfFunction.put("modulo", parameterNumberNumberNumber);

        // on Booleans
        String[] parameterBooleanBooleanBoolean = {"Boolean", "Boolean", "Boolean"};
        parametersOfFunction.put("boolean=?", parameterBooleanBooleanBoolean);

        String[] parameterBooleanANY = {"Boolean", "ANY"};
        parametersOfFunction.put("boolean?", parameterBooleanANY);

        String[] parameterBooleanBoolean = {"Boolean", "Boolean"};
        parametersOfFunction.put("false?", parameterBooleanBoolean);
        parametersOfFunction.put("not", parameterBooleanBoolean);

        // on Lists
        String[] parameterListListListINFINITE = new String[]{"List", "List", "List", "INFINITE"};
        parametersOfFunction.put("append", parameterListListListINFINITE);

        String[] parameterListANYList = new String[]{"List", "ANY", "List"};
        parametersOfFunction.put("cons", parameterListANYList);
        parametersOfFunction.put("remove", parameterListANYList);

        parametersOfFunction.put("empty?", parameterBooleanANY);

        String[] parameterANYList = {"ANY", "List"};
        parametersOfFunction.put("first", parameterANYList);

        String[] parameterNumberList = {"Number", "List"};
        parametersOfFunction.put("length", parameterNumberList);

        String[] parameterListANYINFINITE = {"List", "ANY", "INFINITE"};
        parametersOfFunction.put("list", parameterListANYINFINITE);

        String[] parameterBooleanANYList = {"Boolean", "ANY", "List"};
        parametersOfFunction.put("member", parameterBooleanANYList);

        String[] parameterListNumberNumberNumber = {"List", "Number", "Number", "Number"};
        parametersOfFunction.put("range", parameterListNumberNumberNumber);

        // on Posns
        String[] parameterPosnANYANY = {"Name", "ANY", "ANY"};
        parametersOfFunction.put("make-posn", parameterPosnANYANY);

        String[] parameterANYPosn = {"ANY", "Name"};
        parametersOfFunction.put("posn-x", parameterANYPosn);
        parametersOfFunction.put("posn-y", parameterANYPosn);


        // pre-defined Variables
        parameterOfVariable.put("empty", "List");
        parameterOfVariable.put("pi", "Number");
        parameterOfVariable.put("null", "List");
        parameterOfVariable.put("e", "HashName");
    }


    public void check(String rktString) {
        int[] brackets = bracketCheck(rktString);
        String errorMessage = syntaxCheck(rktString);
    }

    /**
     * Counts the brackets in a String.
     * Opening bracket found means + 1, closing bracket found means - 1.
     * Supports Round, Square, Curly and Angle brackets.
     * @param rktString The String to be checked
     * @return          An Integer Array with counts for every bracket type
     *                  {Round, Square, Curly, Angle}
     */
    public int[] bracketCheck(String rktString) {
        int[] count = new int[4];
        int countIndex = 0;

        String openingBracket = "";
        String closingBracket = "";
        for (BracketType bracketType : BracketType.values()) {
            int checkFromIndex = 0;
            int openingPosition = 0;
            int closingPosition = 0;
            boolean openQuotationMarks = false;

            switch (bracketType) {
                case ROUND -> {
                    openingBracket = "(";
                    closingBracket = ")";
                    countIndex = 0;
                }
                case SQUARE -> {
                    openingBracket = "[";
                    closingBracket = "]";
                    countIndex = 1;
                }
                case CURLY -> {
                    openingBracket = "{";
                    closingBracket = "}";
                    countIndex = 2;
                }
                case ANGLE -> {
                    openingBracket = "<";
                    closingBracket = ">";
                    countIndex = 3;
                }
            }

            while (checkFromIndex<rktString.length() &&
                    (rktString.substring(checkFromIndex, rktString.length()).contains(openingBracket) ||
                            rktString.substring(checkFromIndex, rktString.length()).contains(closingBracket))) {

                openingPosition = rktString.indexOf(openingBracket, checkFromIndex);       // positive zahl oder -1
                closingPosition = rktString.indexOf(closingBracket, checkFromIndex);       // positive zahl oder -1

                String inFrontOfIndex;  // Der String vor dem Index, soll auf Anführungszeichen überprüft werden
                if (openingPosition>=0 && closingPosition>=0) {
                    inFrontOfIndex = rktString.substring(0, Math.min(openingPosition, closingPosition));
                } else if (openingPosition>=0) {
                    inFrontOfIndex = rktString.substring(0, openingPosition);
                } else {
                    inFrontOfIndex = rktString.substring(0, closingPosition);
                }

                openQuotationMarks = countQuotationsMarks(inFrontOfIndex); // Ob vor der Klammer die Summe aller Anführungszeichen ungerade ist


                if (!openQuotationMarks) {
                    if (openingPosition == -1) {        // Es gibt KEINE öffnende Klammer
                        count[countIndex]--;
                        checkFromIndex = closingPosition + 1;
                    } else {
                        if (closingPosition == -1) {
                            count[countIndex]++;
                            checkFromIndex = openingPosition + 1;
                        } else {
                            if (openingPosition < closingPosition) {
                                count[countIndex]++;
                                checkFromIndex = openingPosition + 1;
                            } else {
                                count[countIndex]--;
                                checkFromIndex = closingPosition + 1;
                            }
                        }
                    }
                } else {
                    if (openingPosition>=0 && closingPosition>=0) {
                        checkFromIndex = Math.min(openingPosition, closingPosition) + 1;
                    } else if (openingPosition>=0) {
                        checkFromIndex = openingPosition + 1;
                    } else {
                        checkFromIndex = closingPosition + 1;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Assist bracketCheck. If there is an odd number of quotation marks
     * in a given String, returns true
     * @param s The String to be checked
     * @return  If there are an even number of quotation marks, returns false
     *          If there are an odd number of quotations marks, returns true
     */
    public boolean countQuotationsMarks(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\"') {
                count++;
            }
        }
        return count % 2 != 0;
    }

    public void objectsDefined () {}

    public void nullTeiler() {}


    /**
     * Makes XML-String from the given Racket-String
     * and divides the given Racket-Syntax into Expressions or Definitions
     * and checks them seperatly
     * @param rktString A String with Racket-Syntax
     * @return          A String containing an error-message, if there is a syntax-error in rktString
     */
    public String syntaxCheck(String rktString) {
        String errorMessage = "";
        try {
            DrRacketInterpreter interpreter = new DrRacketInterpreter(rktString);
            //System.out.println(interpreter.getXml());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(interpreter.getXml())));

            // root = drracket
            Element root = document.getDocumentElement();

            NodeList rootChildren = root.getChildNodes();
            rootChildren = removeEmptyText(rootChildren);
            for (int childrenOfRootCount = 0; childrenOfRootCount < rootChildren.getLength(); childrenOfRootCount++) {
                Element defOrExpr = (Element) rootChildren.item(childrenOfRootCount);
                errorMessage = defAndExprCheck(defOrExpr);

                if (! errorMessage.isEmpty()) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errorMessage;
    }

    /**
     * Decides wether a given Element marks the start of a Definition or an Expression.
     * Checks them each a different way.
     * @param defOrExpr An Element containing either an opening-Bracket or an Expression
     * @return          A String containing an error-message, if there is a syntax-error the given Definition or Expression (without Brackets).
     */
    public String defAndExprCheck(Element defOrExpr) {

        if (emptyListCheck(defOrExpr)) {
            return "";
        }

        String typeDefOrExpr = defOrExpr.getAttribute("type");
        switch (typeDefOrExpr) {
            case "round":
                NodeList childrenOfDefOrExpr = defOrExpr.getChildNodes();
                childrenOfDefOrExpr = removeEmptyText(childrenOfDefOrExpr);
                Element firstElement = (Element) childrenOfDefOrExpr.item(0);
                String typeFirstElement = firstElement.getAttribute("type");
                return switch (typeFirstElement) {
                    case "define", "define-struct" -> definitionCheck(defOrExpr);
                    case "Name" -> expressionCheck(defOrExpr);
                    default -> "function call: expected a function after the open parenthesis";
                };
            case "Name":
                return expressionCheck(defOrExpr);
            case "Number":
            case "HashName":
            case "String":
            case "Boolean":
            case "Character":
                return "";
            default:
                return "something unexpected happened: " + typeDefOrExpr;
        }
    }

    /**
     * Decides wether the given Expression is a Function, Condition, if-statement, and-statement, or or-statement.
     * Checks most of them in a seperate way.
     * @param expression An Element containing an opening-Bracket, that marks the start of an Expression.
     * @return          A String containing an error-message, if there is a syntax-error the given Expression
     */
    public String expressionCheck(Element expression) {
        String errorMessage = "";

        String expressionValue = expression.getAttribute("value");
        if (! expressionValue.isEmpty()) {
            if (knownFunctionsList.contains(expressionValue)) {
                return expressionValue + ": expects a function call, but there is no open paranthesis before this function";
            } else if (! knownVariablesList.contains(expressionValue)) {
                return expressionValue + ": this variable is not defined";
            } else {
                return "";
            }

        }
        if (knownVariablesList.contains(expressionValue)) {
            return expressionValue + ": this variable is not defined";
        }

        NodeList expressionChildren = expression.getChildNodes();
        expressionChildren = removeEmptyText(expressionChildren);
        Element expressionFirstChild = (Element) expressionChildren.item(0);
        String functionName = expressionFirstChild.getAttribute("value");
        switch (functionName) {
            case "cond":
                errorMessage = conditionCheck(expression);
                break;
            case "if":
                errorMessage = ifStatementCheck(expression);
                break;
            case "and":
                errorMessage = andStatementCheck(expression);
                break;
            case "or":
                errorMessage = orStatementCheck(expression);
                break;
            default:
                if (knownFunctionsList.contains(functionName)) {
                    errorMessage = functionCheck(expression);
                    break;
                }
                return functionName + ": this function is not defined";
        }

        return errorMessage;
    }

    public String conditionCheck(Element condition) {
        return null;
    }

    public String ifStatementCheck(Element ifStatement) {
        return null;
    }

    public String andStatementCheck(Element andStatement) {
        return null;
    }

    public String orStatementCheck(Element orStatement) {
        return null;
    }

    /**
     * Checks the given Function for syntax-errors.
     * @param function An Element containing an opening-Bracket, that marks the start of a Function.
     * @return          A String containing an error-message, if there is a syntax-error the given Function
     */
    public String functionCheck(Element function) {
        String errorMessage = "";
        int caseInfiniteIndex = -1;

        NodeList functionChildren = function.getChildNodes();
        Element functionFirstChild = (Element) functionChildren.item(0);
        String functionName = functionFirstChild.getAttribute("value");
        NodeList functionParameter = removeEmptyText(functionChildren);

        // A few pre-defined-functions can hold an infinite amount of arguments/attributes/parameters.
        if (parametersOfFunction.get(functionName)[parametersOfFunction.get(functionName).length - 1].equals("INFINITE")) {
            if (parametersOfFunction.get(functionName).length - 1 > functionParameter.getLength())
            {
                return functionName + ": expects " + (parametersOfFunction.get(functionName).length - 2) + " argument, but found " + (functionParameter.getLength() - 1);
            }
        } else {
            if (parametersOfFunction.get(functionName).length > functionParameter.getLength()
                || parametersOfFunction.get(functionName).length < functionParameter.getLength())
            {
                return functionName + ": expects " + (parametersOfFunction.get(functionName).length - 1) + " argument, but found " + (functionParameter.getLength() - 1);
            }
        }

        String expectedParameterType = "";
        for (int parameterCount = 1; parameterCount < functionParameter.getLength(); parameterCount++) {
            Element parameter = (Element) functionParameter.item(parameterCount);
            String parameterType = parameter.getAttribute("type");
            String parameterValue = parameter.getAttribute("value");
            String parameterTag = parameter.getTagName();

            if (parameterCount < parametersOfFunction.get(functionName).length) {
                expectedParameterType = parametersOfFunction.get(functionName)[parameterCount];
            }

            if (expectedParameterType.equals("INFINITE") && caseInfiniteIndex < 0) {
                caseInfiniteIndex = parameterCount - 1;
            }


            if (caseInfiniteIndex >= 0) {
                expectedParameterType = parametersOfFunction.get(functionName)[caseInfiniteIndex];
            }

            if (parameterTag.equals("quote") && emptyListCheck(parameter) && ! expectedParameterType.equals("ANY")) {
                if (! expectedParameterType.equals("List")) {
                    return functionName + ": expects a " + expectedParameterType + ", given List";
                } else {
                    System.out.println("dann halt hier");
                    continue;
                }
            }

            if (! expectedParameterType.equals("ANY")) {
                switch (parameterType) {
                    case "round":
                        NodeList nestedExpression = parameter.getChildNodes();
                        nestedExpression = removeEmptyText(nestedExpression);
                        Element nestedExpressionElement = (Element) nestedExpression.item(0);
                        String nestedExpressionValue = nestedExpressionElement.getAttribute("value");
                        String nestedExpressionType = nestedExpressionElement.getAttribute("type");
                        String nestedReturnType = parametersOfFunction.get(nestedExpressionValue)[0];

                        switch (nestedExpressionType) {
                            case "Name":
                                if (! expectedParameterType.equals(nestedReturnType)) {
                                    return functionName + ": expects a " + expectedParameterType + ", given " + nestedReturnType;
                                }
                                break;
                        }
                        errorMessage = expressionCheck(parameter);
                        break;

                    case "Number":
                    case "HashName":
                    case "String":
                    case "Boolean":
                    case "Character":
                        if (! expectedParameterType.equals(parameterType)) {
                            return functionName + ": expects a " + expectedParameterType + ", given " + parameterValue;
                        }
                        break;
                    case "Name":
                        if (knownVariablesList.contains(parameterValue)) {
                            if (! expectedParameterType.equals(parameterOfVariable.get(parameterValue))) {
                                return functionName + ": expects a " + expectedParameterType + ", given " + parameterValue;
                            }
                        } else {
                            return parameterValue + ": this variable is not defined";
                        }

                }
            } else {
                if (parameterType.equals("Name") && ! knownVariablesList.contains(parameterValue)) {
                    return parameterValue + ": this variable is not defined";
                }
            }
        }
        return errorMessage;
    }


    public String definitionCheck(Element definition) {
        return null;
    }


    /**
     * Removes #Text elements in a NodeList
     */
    public NodeList removeEmptyText(NodeList nodeList) {
        for (int i=0; i<nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE){
                n.getParentNode().removeChild(n);
            }
        }
        return nodeList;
    }

    public boolean emptyListCheck(Element element) {
        if (element.getTagName().equals("quote")) {
            NodeList childrenOfdefOrExpr = element.getChildNodes();
            childrenOfdefOrExpr = removeEmptyText(childrenOfdefOrExpr);
            if (childrenOfdefOrExpr.getLength() == 1) {
                Element child = (Element) childrenOfdefOrExpr.item(0);
                String childType = child.getAttribute("type");
                if (childType.equals("round")) {
                    return true;
                }
            }
        }
        return false;
    }

}
