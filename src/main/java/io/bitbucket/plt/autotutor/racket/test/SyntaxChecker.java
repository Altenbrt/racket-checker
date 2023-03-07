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
    ArrayList<String> tokens;
    String[] knownFunctions = new String[]{"+", "-", "*", "/", "<", "<=", "=", ">", ">=", "abs", "add1", "ceiling", "even?", "exp",
                                        "expt", "floor", "log", "max", "min", "modulo", "negative?", "odd?", "positive?", "random",
                                        "round", "sqr", "sqrt", "sub1", "zero?"};
    List<String> knownFunctionsList;
    HashMap<String, Object[]> parametersOfFunction;

    public SyntaxChecker() {
        knownFunctionsList = Arrays.asList(knownFunctions);
        Object[] parameterNumberTwoTillInfinite = {"Number", 2, Integer.MAX_VALUE};
        parametersOfFunction = new HashMap<>();
        parametersOfFunction.put("+", parameterNumberTwoTillInfinite);
        parametersOfFunction.put("-", parameterNumberTwoTillInfinite);
        parametersOfFunction.put("*", parameterNumberTwoTillInfinite);
        parametersOfFunction.put("/", parameterNumberTwoTillInfinite);
        parametersOfFunction.put("<", parameterNumberTwoTillInfinite);
        parametersOfFunction.put("<=", parameterNumberTwoTillInfinite);
        parametersOfFunction.put(">", parameterNumberTwoTillInfinite);
        parametersOfFunction.put(">=", parameterNumberTwoTillInfinite);

        Object[] parameterNumberOneTillInfinite = {"Number", 1, Integer.MAX_VALUE};
        parametersOfFunction.put("max", parameterNumberOneTillInfinite);
        parametersOfFunction.put("min", parameterNumberOneTillInfinite);

        Object[] parameterNumberOneTillOne = {"Number", 1, 1};
        parametersOfFunction.put("abs", parameterNumberOneTillOne);
        parametersOfFunction.put("add1", parameterNumberOneTillOne);
        parametersOfFunction.put("ceiling", parameterNumberOneTillOne);
        parametersOfFunction.put("even?", parameterNumberOneTillOne);
        parametersOfFunction.put("odd?", parameterNumberOneTillOne);
        parametersOfFunction.put("exp", parameterNumberOneTillOne);
        parametersOfFunction.put("floor", parameterNumberOneTillOne);
        parametersOfFunction.put("log", parameterNumberOneTillOne);
        parametersOfFunction.put("negative?", parameterNumberOneTillOne);
        parametersOfFunction.put("positive?", parameterNumberOneTillOne);
        parametersOfFunction.put("random", parameterNumberOneTillOne);
        parametersOfFunction.put("round", parameterNumberOneTillOne);
        parametersOfFunction.put("sqr", parameterNumberOneTillOne);
        parametersOfFunction.put("sqrt", parameterNumberOneTillOne);
        parametersOfFunction.put("sub1", parameterNumberOneTillOne);
        parametersOfFunction.put("zero?", parameterNumberOneTillOne);

        Object[] parameterNumberTwoTillTwo = {"Number", 2, 2};
        parametersOfFunction.put("expt", parameterNumberTwoTillTwo);
        parametersOfFunction.put("modulo", parameterNumberTwoTillTwo);
    }


    public void check(String rktString) {
        int[] brackets = bracketCheck(rktString);

    }

    public void tokenizeString(String rktString) {
        String[] lines = rktString.trim().split("\n");
        ArrayList<String> tempTokens = new ArrayList<>();
        for (int i = 0; i< lines.length; i++) {
            tempTokens.addAll(Arrays.asList(lines[i].split("\\s+")));
        }

        tokens = new ArrayList<>();
        ArrayList<String> tempTokens2 = new ArrayList<>();
        for (int i = 0; i < tempTokens.size(); i++) {
            tempTokens2.addAll(Arrays.asList(tempTokens.get(i).split("(?<=\\()")));
        }

        for (int i = 0; i < tempTokens2.size(); i++) {
            tokens.addAll(Arrays.asList(tempTokens2.get(i).split("(?=\\))")));
        }
        for (int i = 0; i< tokens.size(); i++) {
            System.out.println(tokens.get(i));
        }
    }


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


    public boolean functionNameKnown(String functionName) {
        if (knownFunctionsList.contains(functionName)) {
            return true;
        } else {
            return false;
        }
    }

    public void functionsDefined () {}

    public void objectsDefined () {}

    public void nullTeiler() {}

    public String parameterCheck(String rktString) {
        // TODO hat eingabe den richtigen typen?
        String errorMessage = "";

        try {
            DrRacketInterpreter inter = new DrRacketInterpreter(rktString);
            System.out.println(inter.getXml());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(inter.getXml())));

            Element root = document.getDocumentElement();

            NodeList nodeList = root.getChildNodes();
            nodeList = removeEmptyText(nodeList);
            for (int i=0; i<nodeList.getLength(); i++) {
                Node n = nodeList.item(i);
                NodeList children = n.getChildNodes();
                children = removeEmptyText(children);

                String functionName = "";
                String parameterType = "";
                int minParameters = 0;
                int maxParameters = 0;
                int parametersFound = 0;
                for (int j=0; j<children.getLength(); j++) {
                    Element child = (Element) children.item(j);
                    switch (child.getAttribute("type")) {
                        case "Name":
                            if (j==0) {
                                if (knownFunctionsList.contains(child.getAttribute("value"))) {
                                    functionName = child.getAttribute("value");
                                    Object[] expectedParameters = parametersOfFunction.get(functionName);
                                    parameterType = (String) expectedParameters[0];
                                    minParameters = (int) expectedParameters[1];
                                    maxParameters = (int) expectedParameters[2];
                                } else {
                                    // TODO How the found Error is handled
                                    return "Function is not defined: " + child.getAttribute("value");
                                }
                            }
                            break;
                        case "Number":
                            if (parameterType.equals("Number")) {
                                parametersFound++;
                            } else {
                                // TODO How the found Error is handled
                                return functionName + ": expects a " + parameterType + ", given " + child.getAttribute("value");
                            }
                            break;
                        case "String":
                            if (parameterType.equals("String")) {
                                parametersFound++;
                            } else {
                                // TODO How the found Error is handled
                                return functionName + ": expects a " + parameterType + ", given " + child.getAttribute("value");
                            }
                            break;
                        case "Boolean":
                            if (parameterType.equals("Boolean")) {
                                parametersFound++;
                            } else {
                                // TODO How the found Error is handled
                                return functionName + ": expects a " + parameterType + ", given " + child.getAttribute("value");
                            }
                            break;
                    }
                    System.out.println(child.getAttribute("type"));
                }

                if (! (parametersFound >= minParameters)) {
                    return functionName + ": expects " + minParameters + " argument, but found " + parametersFound;
                }
                else if (! (parametersFound <= maxParameters)) {
                    return functionName + ": expects " + maxParameters + " argument, but found " + parametersFound;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return errorMessage;

    }

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
