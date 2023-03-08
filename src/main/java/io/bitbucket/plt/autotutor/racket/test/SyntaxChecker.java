package io.bitbucket.plt.autotutor.racket.test;

import io.bitbucket.plt.autotutor.racket.interpret.DrRacketInterpreter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SyntaxChecker {

    /** Bisher nur Funktionen mit Number als Parameter */
    String[] knownFunctions = new String[]{"+", "-", "*", "/", "<", "<=", "=", ">", ">=", "abs", "add1", "ceiling", "even?", "exp",
                                        "expt", "floor", "log", "max", "min", "modulo", "negative?", "odd?", "positive?", "random",
                                        "round", "sqr", "sqrt", "sub1", "zero?",
                                        "number?"};
    List<String> knownFunctionsList;

    /** Jedem Funktionsnamen wird ein Array zugeordnet mit Angaben zu den Parametern
     *  Das Array hat die Form {Rückgabetyp, Parametertyp, Mindestanzahl, Maximale Anzahl}
     *  TODO Rückgabewert einer Funktion und Parameterliste mit unterschiedlichen Typen
     *  Lösung zunächst mit eindimensionalem Array
     */
    HashMap<String, Object[]> parametersOfFunction;

    /**
     * Creates the Parameter-Arrays for every Function and assigns them to the function name
     */
    public SyntaxChecker() {
        knownFunctionsList = Arrays.asList(knownFunctions);
        parametersOfFunction = new HashMap<>();

        Object[] parameterNumberNumberTwoInfinite = {"Number", "Number", 2, Integer.MAX_VALUE};
        parametersOfFunction.put("+", parameterNumberNumberTwoInfinite);
        parametersOfFunction.put("-", parameterNumberNumberTwoInfinite);
        parametersOfFunction.put("*", parameterNumberNumberTwoInfinite);
        parametersOfFunction.put("/", parameterNumberNumberTwoInfinite);

        Object[] parameterBooleanNumberTwoInfinite = {"Boolean", "Number", 2, Integer.MAX_VALUE};
        parametersOfFunction.put("<", parameterBooleanNumberTwoInfinite);
        parametersOfFunction.put("<=", parameterBooleanNumberTwoInfinite);
        parametersOfFunction.put(">", parameterBooleanNumberTwoInfinite);
        parametersOfFunction.put(">=", parameterBooleanNumberTwoInfinite);

        Object[] parameterNumberumberOneInfinite = {"Number", "Number", 1, Integer.MAX_VALUE};
        parametersOfFunction.put("max", parameterNumberumberOneInfinite);
        parametersOfFunction.put("min", parameterNumberumberOneInfinite);

        Object[] parameterNumberNumberOneOne = {"Number", "Number", 1, 1};
        parametersOfFunction.put("abs", parameterNumberNumberOneOne);
        parametersOfFunction.put("add1", parameterNumberNumberOneOne);
        parametersOfFunction.put("ceiling", parameterNumberNumberOneOne);
        parametersOfFunction.put("floor", parameterNumberNumberOneOne);
        parametersOfFunction.put("random", parameterNumberNumberOneOne);
        parametersOfFunction.put("round", parameterNumberNumberOneOne);
        parametersOfFunction.put("sqr", parameterNumberNumberOneOne);
        parametersOfFunction.put("sqrt", parameterNumberNumberOneOne);
        parametersOfFunction.put("sub1", parameterNumberNumberOneOne);

        Object[] parameterBooleanNumberOneOne = {"Boolean", "Number", 1, 1};
        parametersOfFunction.put("even?", parameterBooleanNumberOneOne);
        parametersOfFunction.put("odd?", parameterBooleanNumberOneOne);
        parametersOfFunction.put("negative?", parameterBooleanNumberOneOne);
        parametersOfFunction.put("positive?", parameterBooleanNumberOneOne);
        parametersOfFunction.put("zero?", parameterBooleanNumberOneOne);

        Object[] parameterHashnameNumberOneOne = {"HashName", "Number", 1, 1};
        parametersOfFunction.put("exp", parameterHashnameNumberOneOne);
        parametersOfFunction.put("log", parameterHashnameNumberOneOne);


        Object[] parameterNumberNumberTwoTwo = {"Number", "Number", 2, 2};
        parametersOfFunction.put("expt", parameterNumberNumberTwoTwo);
        parametersOfFunction.put("modulo", parameterNumberNumberTwoTwo);

        Object[] parameterBooleanAnyOneOne = {"Boolean", "ANY", 1, 1};
        parametersOfFunction.put("number?", parameterBooleanAnyOneOne);
    }


    public void check(String rktString) {
        int[] brackets = bracketCheck(rktString);

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
                case ROUND:
                    openingBracket = "(";
                    closingBracket = ")";
                    countIndex = 0;
                    break;
                case SQUARE:
                    openingBracket = "[";
                    closingBracket = "]";
                    countIndex = 1;
                    break;
                case CURLY:
                    openingBracket = "{";
                    closingBracket = "}";
                    countIndex = 2;
                    break;
                case ANGLE:
                    openingBracket = "<";
                    closingBracket = ">";
                    countIndex = 3;
                    break;
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
        if (count % 2 == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void objectsDefined () {}

    public void nullTeiler() {}

    /**
     * Checks if a found function name is already defined.
     * If true, checks over the given parameters of that function wether they fit the expected parameters of that function or not.
     * @param rktString String to be checked
     * @return          A message containing information about the function name and wrong parameters,
     *                  of the first function found, that does not fulfill the expected criteria
     */
    public String parameterCheck(String rktString) {
        String errorMessage = "";

        try {
            DrRacketInterpreter inter = new DrRacketInterpreter(rktString);
            System.out.println(inter.getXml());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(inter.getXml())));

            Element root = document.getDocumentElement();

            NodeList initialNodeList = root.getChildNodes();
            initialNodeList = removeEmptyText(initialNodeList);
            for (int i=0; i<initialNodeList.getLength(); i++) {
                Node initial = initialNodeList.item(i);
                NodeList children = initial.getChildNodes();
                children = removeEmptyText(children);

                String functionName = "";
                // Jedes Objekt-Array hat folgende Struktur: {Typ, min, max}
                ArrayList<Object[]> parameterExpected = new ArrayList<>();
                // Jedes Objekt-Array hat folgende Struktur: {Typ, count}
                ArrayList<Object[]> parameterGiven = new ArrayList<>();
                Object[] givenTypeMinMax = new Object[]{"", 0};
                for (int j=0; j<children.getLength(); j++) {

                    Element child = (Element) children.item(j);
                    String type = child.getAttribute("type");
                    String value = child.getAttribute("value");
                    switch (type) {
                        case "Name":
                            if (j==0) {
                                if (knownFunctionsList.contains(value)) {
                                    functionName = value;
                                    Object[] expectedParameters = parametersOfFunction.get(functionName);

                                    Object[] paraMinMax = new Object[3];
                                    for (int k=1; k<expectedParameters.length; k++) {
                                        if ((k-1)%3==0) {
                                            paraMinMax[0] = expectedParameters[k];
                                        } else if ((k-1)%3==1) {
                                            paraMinMax[1] = expectedParameters[k];
                                        } else {
                                            paraMinMax[2] = expectedParameters[k];
                                            parameterExpected.add(paraMinMax);
                                            paraMinMax = new Object[3];
                                        }
                                    }
                                } else {
                                    return child.getAttribute("value") + ": this function is not defined";
                                }
                            }
                            break;
                        case "Number":
                        case "HashName":
                            int index = -1;
                            boolean matchFound = false;
                            for (Object[] array : parameterExpected){
                                index++;
                                if (array[0].equals("Number") || array[0].equals("HashName")) {
                                    givenTypeMinMax[0] = array[0];
                                    int count = (int) givenTypeMinMax[1];
                                    givenTypeMinMax[1] = count + 1;
                                    parameterGiven.add(givenTypeMinMax);
                                    matchFound = true;
                                    break;
                                }
                            }
                            if (matchFound) break;
                            return functionName + ": expects a " + parameterExpected.get(index)[0] + ", given " + value;
                        case "String":
                            index = -1;
                            matchFound = false;
                            for (Object[] array : parameterExpected) {
                                index++;
                                System.out.println(array[0]);
                                if (array[0].equals("String")) {
                                    givenTypeMinMax[0] = array[0];
                                    int count = (int) givenTypeMinMax[1];
                                    givenTypeMinMax[1] = count + 1;
                                    parameterGiven.add(givenTypeMinMax);
                                    matchFound = true;
                                    break;
                                }
                            }
                            if (matchFound) break;
                            return functionName + ": expects a " + parameterExpected.get(index)[0] + ", given " + value;
                        case "Boolean":
                            index = -1;
                            matchFound = false;
                            for (Object[] array : parameterExpected) {
                                index++;
                                if (array[0].equals("Boolean")) {
                                    givenTypeMinMax[0] = array[0];
                                    int count = (int) givenTypeMinMax[1];
                                    givenTypeMinMax[1] = count + 1;
                                    parameterGiven.add(givenTypeMinMax);
                                    matchFound = true;
                                    break;
                                }
                            }
                            if (matchFound) break;
                            return functionName + ": expects a " + parameterExpected.get(index)[0] + ", given " + value;
                        case "round":
                            NodeList nestedFunction = child.getChildNodes();
                            nestedFunction = removeEmptyText(nestedFunction);
                            Element nestedFunctionElement = (Element) nestedFunction.item(0);
                            String nestedFunctionName = nestedFunctionElement.getAttribute("value");
                            Object[] nestedFunctionParameters = parametersOfFunction.get(nestedFunctionName);

                            index = -1;
                            matchFound = false;
                            for (Object[] array : parameterExpected) {
                                index++;
                                if (array[0].equals(nestedFunctionParameters[0])) {
                                    givenTypeMinMax[0] = array[0];
                                    int count = (int) givenTypeMinMax[1];
                                    givenTypeMinMax[1] = count + 1;
                                    parameterGiven.add(givenTypeMinMax);
                                    matchFound = true;
                                    break;
                                }
                            }
                            if (matchFound) break;
                            return functionName + ": expects a " + parameterExpected.get(index)[0] + ", given " + nestedFunctionParameters[0];
                    }
                }

                for (int a=0; a<parameterExpected.size(); a++) {

                    if (parameterGiven.isEmpty()) {
                        if ((int) parameterExpected.get(a)[1] > 0) {
                            return functionName + ": expects " + parameterExpected.get(a)[1] + " argument, but found 0";
                        }
                    } else {
                        String expectedType = (String) parameterExpected.get(a)[0];
                        String givenType = (String) parameterGiven.get(a)[0];
                        if (expectedType.equals(givenType)) {
                            if ((int) parameterExpected.get(a)[1] > (int) parameterGiven.get(a)[1]) {
                                return functionName + ": expects " + parameterExpected.get(a)[1] + " argument, but found " + parameterGiven.get(a)[1];
                            } else if ((int) parameterExpected.get(a)[2] < (int) parameterGiven.get(a)[1]) {
                                return functionName + ": expects " + parameterExpected.get(a)[2] + " argument, but found " + parameterGiven.get(a)[1];
                            }
                        }
                    }
                }

                givenTypeMinMax = new Object[]{"", 0};
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return errorMessage;

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

}
