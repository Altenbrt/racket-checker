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
import java.util.*;

public class SyntaxChecker {

    String[] knownFunctions = new String[]{
                                        // On Numbers
                                        "+", "-", "*", "/", "<", "<=", "=", ">", ">=", "abs", "acos", "add1", "angle", "asin", "atan", "ceiling", "complex?", "conjugate", "cos", "cosh", "current-seconds", "denominator",
                                        "even?", "exact-inexact", "exact?", "exp", "expt", "floor", "gcd", "imag-part", "inexact-exact", "inexact?", "integer-char?", "integer-sqrt?", "integer?", "lcm", "log", "magnitude",
                                        "make-polar", "make-rectangular", "max", "min", "modulo", "negative?", "number->string", "number->string-digits", "number?", "numerator", "odd?", "positive?", "random", "rational?",
                                        "real-part", "real?", "remainder", "round", "sgn", "sin", "sinh", "sqr", "sqrt", "sub1", "tan", "zero?",
                                        // on Booleans
                                        "boolean=?", "boolean?", "false?", "not", "and", "or", "if",
                                        // on Symbols
                                        "symbol->string", "symbol=?", "symbol?",
                                        // on Lists
                                        "append", "assoc", "assq", "caaar", "caadr", "caar", "cadar", "cadddr", "caddr", "cadr", "car", "cdaar", "cdadr", "cdar", "cddar", "cdddr", "cddr", "cdr", "cons", "cons?", "eight",
                                        "empty?", "fifth", "first", "fourth", "length", "list", "list*", "list-ref", "list?", "make-list", "member", "memq", "memq?", "memv", "null?", "range", "remove", "remove-all", "rest",
                                        "reverse", "second", "seventh", "sixth", "third",
                                        // on Posns
                                        "make-posn", "posn-x", "posn-y", "posn?",
                                        // on Characters
                                        "char->integer", "char-alphabetic?", "char-ci<=?", "char-ci<?", "char-ci=?", "char-ci>=?", "char-ci>?" ,"char-downcase", "char-lower-case?", "char-numeric?", "char-upcase",
                                        "char-upper-case?", "char-whitespace?", "char<=?", "char<?", "char=?", "char>=?", "char>?", "char?",
                                        // on Strings
                                        "explode", "format", "implode","int->string","list->string","make-string","replicate","string","string->int","string->list","string->number","string->symbol","string-alphabetic?",
                                        "string-append","string-ci<=?","string-ci<?","string-ci=?","string-ci>=?","string-ci>?","string-contains-ci?","string-contains?","string-copy","string-downcase","string-ith",
                                        "string-length","string-lower-case?","string-numeric?","string-ref","string-upcase","string-upper-case?","string-whitespace?","string<=?","string<?","string=?","string>=?",
                                        "string>?","string?","substring",
                                        // special
                                        "cond"};

    /** Alle definierten Variabeln, sowohl vordefiniert, als auch selber definierte */
    String[] knownVariables = new String[]{"empty", "pi", "null", "e"};
    ArrayList<String> knownFunctionsList;
    ArrayList<String> knownVariablesList;

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
        List<String> listF = Arrays.asList(knownFunctions);
        List<String> listV = Arrays.asList(knownVariables);
        knownFunctionsList = new ArrayList<>(listF);
        knownVariablesList = new ArrayList<>(listV);
        parametersOfFunction = new HashMap<>();
        parameterOfVariable = new HashMap<>();

        // special
        String[] parameterANYNameINIFINITE = {"ANY", "Name", "INFINITE"};
        parametersOfFunction.put("cond", parameterANYNameINIFINITE);

        // pre-defined Variables
        parameterOfVariable.put("empty", "List");
        parameterOfVariable.put("pi", "Number");
        parameterOfVariable.put("null", "List");
        parameterOfVariable.put("e", "HashName");

        // On numbers
        String[] parameterNumberNumberNumberInfinite = {"Number", "Number", "Number", "INFINITE"};
        String[] parameterBooleanNumberNumberInfinite = {"Boolean", "Number", "Number", "INFINITE"};
        String[] parameterNumberNumberInfinite = {"Number", "Number", "INFINITE"};
        String[] parameterNumberNumber = {"Number", "Number"};
        String[] parameterBooleanNumber = {"Boolean", "Number"};
        String[] parameterNumber = {"Number"};
        String[] parameterHashnameNumber = {"HashName", "Number"};
        String[] parameterNumberNumberNumber = {"Number", "Number", "Number"};
        String[] parameterCharacterNumber = {"Character", "Number"};
        String[] parameterBooleanANY = {"Boolean", "ANY"};
        String[] parameterHashnameHashname = {"HashName", "HashName"};
        String[] parameterHashnameNumberNumber = {"HashName", "Number", "Number"};
        String[] parameterStringNumber = {"String", "Number"};
        String[] parameterStringNumberNumber = {"String", "Number", "Number"};
        parametersOfFunction.put("+", parameterNumberNumberNumberInfinite);
        parametersOfFunction.put("*", parameterNumberNumberNumberInfinite);
        parametersOfFunction.put("/", parameterNumberNumberNumberInfinite);
        parametersOfFunction.put("-", parameterNumberNumberInfinite);
        parametersOfFunction.put("<", parameterBooleanNumberNumberInfinite);
        parametersOfFunction.put("<=", parameterBooleanNumberNumberInfinite);
        parametersOfFunction.put(">", parameterBooleanNumberNumberInfinite);
        parametersOfFunction.put(">=", parameterBooleanNumberNumberInfinite);
        parametersOfFunction.put("=", parameterBooleanNumberNumberInfinite);
        parametersOfFunction.put("abs", parameterNumberNumber);
        parametersOfFunction.put("acos", parameterNumberNumber);
        parametersOfFunction.put("add1", parameterNumberNumber);
        parametersOfFunction.put("angle", parameterNumberNumber);
        parametersOfFunction.put("asin", parameterNumberNumber);
        parametersOfFunction.put("atan", parameterNumberNumber);
        parametersOfFunction.put("ceiling", parameterNumberNumber);
        parametersOfFunction.put("complex?", parameterBooleanANY);
        parametersOfFunction.put("conjugate", parameterNumberNumber);
        parametersOfFunction.put("cos", parameterNumberNumber);
        parametersOfFunction.put("cosh", parameterNumberNumber);
        parametersOfFunction.put("current-seconds", parameterNumber);
        parametersOfFunction.put("denominator", parameterNumberNumber);
        parametersOfFunction.put("even?", parameterBooleanNumber);
        parametersOfFunction.put("exact-inexact", parameterNumberNumber);
        parametersOfFunction.put("exact?", parameterBooleanNumber);
        parametersOfFunction.put("exp", parameterHashnameNumber);
        parametersOfFunction.put("expt", parameterNumberNumberNumber);
        parametersOfFunction.put("floor", parameterNumberNumber);
        parametersOfFunction.put("gcd", parameterNumberNumberNumber);
        parametersOfFunction.put("imag-part", parameterNumberNumber);
        parametersOfFunction.put("inexact-exact", parameterNumberNumber);
        parametersOfFunction.put("inexact?", parameterBooleanNumber);
        parametersOfFunction.put("integer-char?", parameterCharacterNumber);
        parametersOfFunction.put("integer-sqrt?", parameterNumberNumber);       // complex ist Name !!!!!!!!!!!!
        parametersOfFunction.put("integer?", parameterBooleanANY);
        parametersOfFunction.put("lcm", parameterNumberNumberInfinite);
        parametersOfFunction.put("log", parameterHashnameNumber);
        parametersOfFunction.put("magnitude", parameterHashnameHashname);
        parametersOfFunction.put("make-polar", parameterHashnameNumberNumber);
        parametersOfFunction.put("make-rectangular", parameterHashnameNumberNumber);
        parametersOfFunction.put("max", parameterNumberNumberInfinite);
        parametersOfFunction.put("min", parameterNumberNumberInfinite);
        parametersOfFunction.put("modulo", parameterNumberNumberNumber);
        parametersOfFunction.put("negative?", parameterBooleanNumber);
        parametersOfFunction.put("number->string", parameterStringNumber);
        parametersOfFunction.put("number->string-digits", parameterStringNumberNumber);
        parametersOfFunction.put("number?", parameterBooleanANY);
        parametersOfFunction.put("numerator", parameterNumberNumber);
        parametersOfFunction.put("odd?", parameterBooleanNumber);
        parametersOfFunction.put("positive?", parameterBooleanNumber);
        parametersOfFunction.put("quotient", parameterNumberNumberNumber);
        parametersOfFunction.put("random", parameterNumberNumber);
        parametersOfFunction.put("rational?", parameterBooleanANY);
        parametersOfFunction.put("real-part", parameterNumberNumber);
        parametersOfFunction.put("real?", parameterBooleanANY);
        parametersOfFunction.put("remainder", parameterNumberNumberNumber);
        parametersOfFunction.put("round", parameterNumberNumber);
        parametersOfFunction.put("sgn", parameterNumberNumber);
        parametersOfFunction.put("sin", parameterNumberNumber);
        parametersOfFunction.put("sinh", parameterNumberNumber);
        parametersOfFunction.put("sqr", parameterNumberNumber);
        parametersOfFunction.put("sqrt", parameterNumberNumber);
        parametersOfFunction.put("sub1", parameterNumberNumber);
        parametersOfFunction.put("tan", parameterNumberNumber);
        parametersOfFunction.put("zero?", parameterBooleanNumber);


        // on Booleans
        String[] parameterStringBoolean = {"String", "Boolean"};
        String[] parameterBooleanBooleanBoolean = {"Boolean", "Boolean", "Boolean"};
        String[] parameterBooleanBoolean = {"Boolean", "Boolean"};
        parametersOfFunction.put("boolean->string", parameterStringBoolean);
        parametersOfFunction.put("boolean=?", parameterBooleanBooleanBoolean);
        parametersOfFunction.put("boolean?", parameterBooleanANY);
        parametersOfFunction.put("false?", parameterBooleanANY);
        parametersOfFunction.put("not", parameterBooleanBoolean);

        String[] parameterBooleanBooleanBooleanINFNITE = {"Boolean", "Boolean", "Boolean", "INFINITE"};
        String[] parameterANYBooleanANYANY = {"ANY", "Boolean", "ANY", "ANY"};
        parametersOfFunction.put("and", parameterBooleanBooleanBooleanINFNITE);
        parametersOfFunction.put("or", parameterBooleanBooleanBooleanINFNITE);
        parametersOfFunction.put("if", parameterANYBooleanANYANY);


        // on Symbols
        String[] parameterStringSymbol = {"String", "Symbol"};
        String[] parameterBooleanSymbolSymbol = {"Boolean", "Symbol", "Symbol"};
        parametersOfFunction.put("symbol->string", parameterStringSymbol);
        parametersOfFunction.put("symbol=?", parameterBooleanSymbolSymbol);
        parametersOfFunction.put("symbol?", parameterBooleanANY);


        // on Lists
        String[] parameterListListListINFINITE = new String[]{"List", "List", "List", "INFINITE"};
        String[] parameterListANYList = new String[]{"List", "ANY", "List"};
        String[] parameterANYList = {"ANY", "List"};
        String[] parameterListANYINFINITEList = {"List", "ANY", "INFINITE", "List"};
        String[] parameterNumberList = {"Number", "List"};
        String[] parameterListANYINFINITE = {"List", "ANY", "INFINITE"};
        String[] parameterBooleanANYList = {"Boolean", "ANY", "List"};
        String[] parameterListNumberNumberNumber = {"List", "Number", "Number", "Number"};
        String[] parameterANYListNumber = {"ANY", "List", "Number"};
        String[] parameterListNumberANY = {"List", "Number", "ANY"};
        String[] parameterListList = {"List", "List"};
        parametersOfFunction.put("append", parameterListListListINFINITE);
        parametersOfFunction.put("assoc", parameterListANYList);    // maybe not checkable -->
        parametersOfFunction.put("assq", parameterListANYList);
        parametersOfFunction.put("caaar", parameterANYList);
        parametersOfFunction.put("caadr", parameterANYList);
        parametersOfFunction.put("caar", parameterANYList);
        parametersOfFunction.put("cadar", parameterANYList);
        parametersOfFunction.put("cadddr", parameterANYList);
        parametersOfFunction.put("caddr", parameterANYList);
        parametersOfFunction.put("cadr", parameterANYList);     // <-- maybe not checkable, has to be checked if the program is executed
        parametersOfFunction.put("car", parameterANYList);
        parametersOfFunction.put("cdaar", parameterANYList);    // maybe not checkable in syntaxCheck -->
        parametersOfFunction.put("cdadr", parameterANYList);
        parametersOfFunction.put("cdar", parameterListList);
        parametersOfFunction.put("cddar", parameterANYList);
        parametersOfFunction.put("cdddr", parameterANYList);
        parametersOfFunction.put("cddr", parameterListList);
        parametersOfFunction.put("cdr", parameterANYList);      // <-- maybe not checkable in syntaxCheck
        parametersOfFunction.put("cons", parameterListANYList);
        parametersOfFunction.put("cons?", parameterBooleanANY);
        parametersOfFunction.put("eigth", parameterANYList);
        parametersOfFunction.put("empty?", parameterBooleanANY);
        parametersOfFunction.put("fifth", parameterANYList);
        parametersOfFunction.put("first", parameterANYList);
        parametersOfFunction.put("fourth", parameterANYList);
        parametersOfFunction.put("length", parameterNumberList);
        parametersOfFunction.put("list", parameterListANYINFINITE);
        parametersOfFunction.put("list*", parameterListANYINFINITEList);        // Hier gibts noch ein großes Problem
        parametersOfFunction.put("list-ref", parameterANYListNumber);
        parametersOfFunction.put("list?", parameterBooleanANY);
        parametersOfFunction.put("make-list", parameterListNumberANY);
        parametersOfFunction.put("member", parameterBooleanANYList);
        parametersOfFunction.put("memq", parameterBooleanANYList);
        parametersOfFunction.put("memq?", parameterBooleanANYList);
        parametersOfFunction.put("memv?", parameterBooleanANYList);         // kann sowohl Boolean als auch List ausgeben
        parametersOfFunction.put("null?", parameterBooleanANY);
        parametersOfFunction.put("range", parameterListNumberNumberNumber);
        parametersOfFunction.put("remove", parameterListANYList);
        parametersOfFunction.put("remove-all", parameterListANYList);
        parametersOfFunction.put("rest", parameterANYList);
        parametersOfFunction.put("reverse", parameterListList);
        parametersOfFunction.put("second", parameterANYList);
        parametersOfFunction.put("seventh", parameterANYList);
        parametersOfFunction.put("sixth", parameterANYList);
        parametersOfFunction.put("third", parameterANYList);


        // on Posns
        String[] parameterPosnANYANY = {"Name", "ANY", "ANY"};
        String[] parameterANYPosn = {"ANY", "Name"};
        parametersOfFunction.put("make-posn", parameterPosnANYANY);
        parametersOfFunction.put("posn-x", parameterANYPosn);
        parametersOfFunction.put("posn-y", parameterANYPosn);
        parametersOfFunction.put("posn?", parameterBooleanANY);


        // on Characters
        String[] parameterNumberCharacter = {"Number", "Character"};
        String[] parameterBooleanCharacter = {"Boolean", "Character"};
        String[] parameterBooleanCharacterINFINITE = {"Boolean", "Character", "INFINITE"};
        String[] parameterCharacterCharacter = {"Character", "Character"};
        parametersOfFunction.put("char->integer", parameterNumberCharacter);
        parametersOfFunction.put("char-alphabetic?", parameterBooleanCharacter);
        parametersOfFunction.put("char-ci<=?", parameterBooleanCharacterINFINITE);
        parametersOfFunction.put("char-ci<?", parameterBooleanCharacterINFINITE);
        parametersOfFunction.put("char-ci=?", parameterBooleanCharacterINFINITE);
        parametersOfFunction.put("char-ci>=?", parameterBooleanCharacterINFINITE);
        parametersOfFunction.put("char-ci>?", parameterBooleanCharacterINFINITE);
        parametersOfFunction.put("char-downcase", parameterCharacterCharacter);
        parametersOfFunction.put("char-lower-case?", parameterBooleanCharacter);
        parametersOfFunction.put("char-numeric?", parameterBooleanCharacter);
        parametersOfFunction.put("char-upcase", parameterCharacterCharacter);
        parametersOfFunction.put("char-upper-case?", parameterBooleanCharacter);
        parametersOfFunction.put("char-whitespace?", parameterBooleanCharacter);
        parametersOfFunction.put("char<=?", parameterBooleanCharacterINFINITE);
        parametersOfFunction.put("char<?", parameterBooleanCharacterINFINITE);
        parametersOfFunction.put("char=?", parameterBooleanCharacterINFINITE);
        parametersOfFunction.put("char>=?", parameterBooleanCharacterINFINITE);
        parametersOfFunction.put("char>?", parameterBooleanCharacterINFINITE);
        parametersOfFunction.put("char?", parameterBooleanANY);


        // on Strings
        String[] parameterListString = {"List", "String"};
        String[] parameterStringStringANY = {"String", "String", "ANY"};
        String[] parameterStringList = {"String", "List"};
        String[] parameterStringNumberCharacter = {"String", "Number", "Character"};
        String[] parameterStringNumberString = {"String", "Number", "String"};
        String[] parameterStringCharacter = {"String", "Character"};
        String[] parameterNumberString = {"Number", "String"};
        String[] parameterSymbolString = {"Symbol", "String"};
        String[] parameterBooleanString = {"Boolean", "String"};
        String[] parameterStringStringStringINFINITE = {"String", "String", "String", "INFINITE"};
        String[] parameterBooleanStringString = {"Boolean", "String", "String"};
        String[] parameterStringString = {"String", "String"};
        String[] parameterStringStringNumber = {"String", "String", "Number"};
        String[] parameterCharacterStringNumber = {"Character", "String", "Number"};
        String[] parameterStringStringNumberNumber = {"String", "String", "Number", "Number"};
        parametersOfFunction.put("explode", parameterListString);
        parametersOfFunction.put("format", parameterStringStringANY);
        parametersOfFunction.put("implode", parameterListString);
        parametersOfFunction.put("int->string", parameterStringNumber);
        parametersOfFunction.put("list->string", parameterStringList);
        parametersOfFunction.put("make-string", parameterStringNumberCharacter);
        parametersOfFunction.put("replicate", parameterStringNumberString);
        parametersOfFunction.put("string", parameterStringCharacter);
        parametersOfFunction.put("string->int", parameterNumberString);
        parametersOfFunction.put("string->list", parameterListString);
        parametersOfFunction.put("string->number", parameterNumberString);
        parametersOfFunction.put("string->symbol", parameterSymbolString);
        parametersOfFunction.put("string-alphabetic?", parameterBooleanString);
        parametersOfFunction.put("string-append", parameterStringStringStringINFINITE);
        parametersOfFunction.put("string-ci<=?", parameterBooleanStringString);
        parametersOfFunction.put("string-ci<?", parameterBooleanStringString);
        parametersOfFunction.put("string-ci=?", parameterBooleanStringString);
        parametersOfFunction.put("string-ci>=?", parameterBooleanStringString);
        parametersOfFunction.put("string-ci>?", parameterBooleanStringString);
        parametersOfFunction.put("string-contains-ci?", parameterBooleanStringString);
        parametersOfFunction.put("string-contains?", parameterBooleanStringString);
        parametersOfFunction.put("string-copy", parameterStringString);
        parametersOfFunction.put("string-downcase", parameterStringString);
        parametersOfFunction.put("string-ith", parameterStringStringNumber);
        parametersOfFunction.put("string-length", parameterNumberString);
        parametersOfFunction.put("string-lower-case?", parameterBooleanString);
        parametersOfFunction.put("string-numeric?", parameterBooleanString);
        parametersOfFunction.put("string-ref", parameterCharacterStringNumber);
        parametersOfFunction.put("string-upcase", parameterStringString);
        parametersOfFunction.put("string-upper-case?", parameterBooleanString);
        parametersOfFunction.put("string-whitespace?", parameterBooleanString);
        parametersOfFunction.put("string<=?", parameterBooleanStringString);
        parametersOfFunction.put("string<?", parameterBooleanStringString);
        parametersOfFunction.put("string=?", parameterBooleanStringString);
        parametersOfFunction.put("string>=?", parameterBooleanStringString);
        parametersOfFunction.put("string>?", parameterBooleanStringString);
        parametersOfFunction.put("string?", parameterBooleanANY);
        parametersOfFunction.put("substring", parameterStringStringNumberNumber);       // Hier gehen entweder 1 Number oder zwei Number


        // TODO Images fehlt noch und MISC fehlt noch


    }


    public void check(String rktString) {
        bracketCheck(rktString);
        divisionByZeroCheck();
        syntaxCheck(rktString);
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

    public void divisionByZeroCheck() {}


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

        // check for '()
        if (emptyListCheck(defOrExpr)) {
            return "";
        }

        String typeDefOrExpr = defOrExpr.getAttribute("type");
        switch (typeDefOrExpr) {
            case "round":   // Could be a Definition or Expression
                NodeList childrenOfDefOrExpr = defOrExpr.getChildNodes();
                childrenOfDefOrExpr = removeEmptyText(childrenOfDefOrExpr);
                Element firstElement = (Element) childrenOfDefOrExpr.item(0);
                String typeFirstElement = firstElement.getAttribute("type");
                String valueFirstElement = firstElement.getAttribute("value");
                if (typeFirstElement.equals("Name")) {
                    if ("define".equals(valueFirstElement) || "define-struct".equals(valueFirstElement)) {
                        return definitionCheck(defOrExpr);
                    } else {
                        if (! knownFunctionsList.contains(valueFirstElement)) {
                            return valueFirstElement + ": this function is not defined";
                        }
                        return expressionCheck(defOrExpr);
                    }
                } else {
                    return "function call: expected a function after the open parenthesis";
                }
            case "Name":    // Must be a Variable
                return expressionCheck(defOrExpr);
            case "Number":
            case "HashName":
            case "String":
            case "Boolean":
            case "Character":
            case "Symbol":
                return "";
            default:
                return "something unexpected happened: " + typeDefOrExpr;
        }
    }

    /**
     * Decides wether the given Expression is a Function, Condition, if-statement, and-statement, or or-statement.
     * Checks most of them in a seperate way.
     * @param expression An Element containing an opening-Bracket, that marks the start of an Expression, or a one-literal-expression.
     * @return          A String containing an error-message, if there is a syntax-error the given Expression
     */
    public String expressionCheck(Element expression) {
        String errorMessage = "";

        String expressionValue = expression.getAttribute("value");
        if (! expressionValue.isEmpty()) {                                  // If the element is not a Bracket
            if (knownFunctionsList.contains(expressionValue)) {             // If the element is a defined function -> it means there is a bracket-mistake
                return expressionValue + ": expects a function call, but there is no open paranthesis before this function";
            } else if (! knownVariablesList.contains(expressionValue)) {    // If the element is not a defined variable -> error
                return expressionValue + ": this variable is not defined";
            } else {                                                        // If the element is a defined variable -> no error
                return "";
            }
        }
        /*
        if (knownVariablesList.contains(expressionValue)) {
            return expressionValue + ": this variable is not defined";
        }
         */

        NodeList expressionChildren = expression.getChildNodes();
        expressionChildren = removeEmptyText(expressionChildren);                         // expressionChildren = functionName + functionAttributes
        Element functionElement = (Element) expressionChildren.item(0);             //                                     functionAttributes = given Parameters
        String functionName = functionElement.getAttribute("value");
        String[] expectedParameters = parametersOfFunction.get(functionName);             // expectedParameters = returnValue + functionAttributes
                                                                                          //                                    functionAttributes = expected Parameters

        if (knownFunctionsList.contains(functionName)) {    // if the given function is defined -> if not it means error
            // Some pre-defined functions can have an ininite amount of attributes. If such a function is detected, this counter will be set to a certain value,
            // so that the attribute can be checked an infinit (Integer.MAX_VALUE) amount of time.
            int caseInfiniteIndex = -1;

            // If there are more attributes less or more attributes in the given expression than in the expected expression
            if (expectedParameters[expectedParameters.length - 1].equals("INFINITE")) {
                if (expectedParameters.length - 1 > expressionChildren.getLength())
                {
                    return functionName + ": expects " + (expectedParameters.length - 2) + " argument, but found " + (expressionChildren.getLength() - 1);
                }
            } else {
                if (expectedParameters.length > expressionChildren.getLength()
                        || expectedParameters.length < expressionChildren.getLength())
                {
                    return functionName + ": expects " + (expectedParameters.length - 1) + " argument, but found " + (expressionChildren.getLength() - 1);
                }
            }

            // Iteration over all given Attributes
            String expectedParameterType = "";
            for (int parameterCount = 1; parameterCount < expressionChildren.getLength(); parameterCount++) {
                Element parameter = (Element) expressionChildren.item(parameterCount);    // Parameter = current Attribute
                String parameterType = parameter.getAttribute("type");              // Type  =  Number | String | Name | round | ...
                String parameterValue = parameter.getAttribute("value");            // Value =  Name of a function | name of an attribute | 1 | "hallo" | ...
                String parameterTag = parameter.getTagName();                             // Tag   =  paren | terminal | quote

                if (parameterCount < expectedParameters.length) {                   // The expected Attribute must have the same position in its String-array,
                    expectedParameterType = expectedParameters[parameterCount];     // as the given Attribute in its NodeList.
                }

                if (expectedParameterType.equals("INFINITE") && caseInfiniteIndex < 0) {    // If a function with infinite possible attributes is detected, the index is changed,
                    caseInfiniteIndex = parameterCount - 1;                                 // so that in every for-iteration the infinite attribute is set as
                }                                                                           // the expectedParameter
                if (caseInfiniteIndex >= 0) {
                    expectedParameterType = expectedParameters[caseInfiniteIndex];
                }

                // if the given attribute is '()
                if (parameterTag.equals("quote") && emptyListCheck(parameter) && ! expectedParameterType.equals("ANY")) {
                    if (! expectedParameterType.equals("List")) {
                        return functionName + ": expects a " + expectedParameterType + ", given List";
                    } else {
                        continue;
                    }
                }

                // if the expression is a condition
                if (functionName.equals("cond")) {
                    if (! parameterType.equals("square")) {
                        if (parameterType.equals("round")) {
                            Element nestedExpressionElement = (Element) removeEmptyText(parameter.getChildNodes()).item(0);
                            String nestedReturnValue = nestedExpressionElement.getAttribute("value");
                            return nestedReturnValue + ": expected a function call, but there is no open parenthesis before this function";
                        }
                        return functionName + ": expected a clause with a question and an answer, but found " + parameterType;
                    } else {
                        // Hier sind eckige Klammern, parameter ist eine eckige Klammer
                        NodeList nestedExpression = parameter.getChildNodes();
                        nestedExpression = removeEmptyText(nestedExpression);   // kinder von der eckigen Klammer, dürfen maximal zwei sein

                        if (nestedExpression.getLength() != 2) {
                            return "cond: expected a clause with a question and an answer, but found a clause with " + nestedExpression.getLength() + " parts";
                        }

                        Element nestedReturnElement = (Element) nestedExpression.item(0);   // Das hier muss ein boolean sein
                        String nestedReturnType = nestedReturnElement.getAttribute("type");
                        String nestedReturnValue = nestedReturnElement.getAttribute("value");
                        String nestedReturnTag = nestedReturnElement.getTagName();
                        if (!nestedReturnType.equals("Boolean")) {
                            if (nestedReturnValue.equals("else")) {
                                if (parameterCount < expressionChildren.getLength() - 1) {
                                    return "cond: found an else clause that isn't the last clause in its cond expression";
                                }
                                Element nestedSecondElement = (Element) nestedExpression.item(1);
                                String nestedSecondType = nestedSecondElement.getAttribute("type");
                                if (nestedSecondType.equals("round")){
                                    errorMessage = expressionCheck((Element) nestedExpression.item(1));
                                    if (!errorMessage.isEmpty()) return errorMessage;
                                } else {
                                    errorMessage = defAndExprCheck((Element) nestedExpression.item(1));
                                    if (!errorMessage.isEmpty()) return errorMessage;
                                }
                                continue;
                            }
                            if (nestedReturnType.equals("Name")) {
                                return "cond: question result is not true or false: " + nestedReturnValue;
                            }
                            if (nestedReturnTag.equals("quote") && emptyListCheck(nestedReturnElement)) {
                                return "cond: question result is not true or false: '()";
                            }

                            if (nestedReturnType.equals("round")) {
                                nestedExpression = removeEmptyText(nestedReturnElement.getChildNodes());
                                nestedReturnElement = (Element) nestedExpression.item(0);
                                if (knownFunctionsList.contains(nestedReturnElement.getAttribute("value"))) {
                                    String returnType = parametersOfFunction.get(nestedReturnElement.getAttribute("value"))[0];
                                    if (! returnType.equals("Boolean")) {
                                        return "cond: question result is not true or false: " + returnType;
                                    } else {
                                        errorMessage = expressionCheck((Element) nestedReturnElement.getParentNode());
                                        if (!errorMessage.isEmpty()) return errorMessage;
                                        continue;
                                    }
                                } else {
                                    return nestedReturnElement.getAttribute("value") + ": this function is not defined";
                                }
                            }

                            return "cond: question result is not true or false: " + nestedReturnType;
                        } else {
                            Element nestedSecondElement = (Element) nestedExpression.item(1);
                            String nestedSecondType = nestedSecondElement.getAttribute("type");
                            if (nestedSecondType.equals("round")){
                                errorMessage = expressionCheck(nestedSecondElement);
                                if (!errorMessage.isEmpty()) return errorMessage;
                                System.out.println(errorMessage);
                            } else {
                                errorMessage = defAndExprCheck((Element) nestedExpression.item(1));
                                if (!errorMessage.isEmpty()) return errorMessage;
                            }
                            continue;
                        }
                    }
                }

                // if the expression is any function call other than condition
                if (! expectedParameterType.equals("ANY")) {    // if the expected attribute excepts every Type, there can only be an error, if the given attribute is a non-defined variable

                    switch (parameterType) {
                        case "round":           // must be a nested function
                            NodeList nestedExpression = parameter.getChildNodes();  // name of nested function + attributes of nested function
                            nestedExpression = removeEmptyText(nestedExpression);
                            Element nestedExpressionElement = (Element) nestedExpression.item(0);   //
                            String nestedExpressionValue = nestedExpressionElement.getAttribute("value");
                            String nestedExpressionType = nestedExpressionElement.getAttribute("type");
                            String nestedReturnType = parametersOfFunction.get(nestedExpressionValue)[0];

                            if (nestedExpressionType.equals("Name")) {          // if the return Value of the nested function does not fit the expected attribute
                                if (! expectedParameterType.equals(nestedReturnType) && ! nestedReturnType.equals("ANY")) {
                                    return functionName + ": expects a " + expectedParameterType + ", given " + nestedReturnType;
                                }
                            }
                            errorMessage = expressionCheck(parameter);      // Recursive call of expressionCheck for the nested function
                            if (!errorMessage.isEmpty()) return errorMessage;
                            break;

                        case "Number":      // If the given attribute is a literal, its Type must fit the expected Type
                        case "HashName":
                        case "String":
                        case "Boolean":
                        case "Character":
                            if (! expectedParameterType.equals(parameterType)) {
                                return functionName + ": expects a " + expectedParameterType + ", given " + parameterValue;
                            }
                            break;
                        case "Name":        // The given literal is either a name or a variable. The Type of the Value of the Name must fit the expected Type
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
        } else {
            return functionName + ": this function is not defined";
        }
    }

    public String definitionCheck(Element definition) {
        String errorMessage = "";

        // definition ist eine klammer
        NodeList definitionChildren = removeEmptyText(definition.getChildNodes());

        if (definitionChildren.getLength() != 3) {
            return "define: expects 2 argument, but found " + (definitionChildren.getLength() - 1);     // in a definition there must always be define, name/expression, expression
        }                                                                                               // if there are less or more than these 3 elements, it cant be a valid definition

        Element defineElement = (Element) definitionChildren.item(0);
        String defineValue = defineElement.getAttribute("value");

        switch (defineValue) {
            case "define":
                Element firstParameterElement = (Element) definitionChildren.item(1);    // Muss ein ungenutzter Name oder eine Klammer sein
                String firstParameterType = firstParameterElement.getAttribute("type");
                String firstParameterValue = firstParameterElement.getAttribute("value");

                Element secondParameterElement = (Element) definitionChildren.item(2);    // Muss ein ungenutzter Name oder eine Klammer sein
                String secondParameterType = secondParameterElement.getAttribute("type");
                String secondParameterValue = secondParameterElement.getAttribute("value");

                switch (firstParameterType) {
                    case "Name":        // Handelt sich um eine Variable
                        if (knownFunctionsList.contains(secondParameterValue) || knownVariablesList.contains(secondParameterValue)) {
                            return secondParameterValue + ": this name was defined previously and cannot be re-defined";    // If the function/variable was already defined
                        }

                        if ("round".equals(secondParameterType)) {  // Rückgabetyp der eingerückten Funktion ist secondParameter, ExpressionCheck muss darauf ausgeführt werden
                            NodeList nestedParameters = removeEmptyText(secondParameterElement.getChildNodes());
                            Element nestedParameterElement = (Element) nestedParameters.item(0);
                            String nestedParameterValue = nestedParameterElement.getAttribute("value");
                            String returnType = parametersOfFunction.get(nestedParameterValue)[0];

                            parameterOfVariable.put(firstParameterValue, returnType);
                            knownVariablesList.add(firstParameterValue);
                            errorMessage = expressionCheck(secondParameterElement);
                            if (! errorMessage.isEmpty()) System.out.println("hier?"); return errorMessage;     // if the assigned expression has syntax errors
                        } else {
                            switch (secondParameterType) {
                                case "Name":    // Muss eine Variable sein
                                    if (! knownVariablesList.contains(secondParameterValue)) {
                                        return secondParameterValue + ": this variable is not defined";
                                    }
                                case "Number":
                                case "Boolean":
                                case "String":
                                case "Character":
                                    break;
                                default:
                                    return secondParameterType + ": not an expression";
                            }
                            parameterOfVariable.put(firstParameterValue, secondParameterType);
                            knownVariablesList.add(firstParameterValue);
                        }
                        break;

                    case "round":
                        //erwartet mindestens zwei Namen, also den Funktionsnamen und mindestens eine Variable
                        NodeList functionNameAndParameters = removeEmptyText(firstParameterElement.getChildNodes());

                        if (functionNameAndParameters.getLength() < 2) {
                            return "define: expected at least one variable after the function name, but found none";
                        }

                        Element functionNameElement = (Element) functionNameAndParameters.item(0);
                        String functionNameType = functionNameElement.getAttribute("type");
                        if (! functionNameType.equals("Name")) {
                            return "define: expected the name of the function, but found a " + functionNameType;
                        }

                        String functionNameValue = functionNameElement.getAttribute("value");
                        if (knownVariablesList.contains(functionNameValue) || knownFunctionsList.contains(functionNameValue)) {
                            return functionNameValue + ": this name cannot be re-defined";
                        }

                        String[] parametersOfFunction = new String[functionNameAndParameters.getLength() - 1];
                        for (int parameterPosition = 1; parameterPosition < functionNameAndParameters.getLength(); parameterPosition++) {
                            Element parameterElement = (Element) functionNameAndParameters.item(parameterPosition);
                            String parameterType = parameterElement.getAttribute("type");

                            switch (parameterType) {
                                case "round":
                                case "square":
                                    return "define: expected a variable, but found a part";
                                case "Number":
                                case "Boolean":
                                case "String":
                                case "Character":
                                    return "define: expected a variable, but found a " + parameterType;
                            }

                            parametersOfFunction[parameterPosition] = parameterType;
                        }

                        this.parametersOfFunction.put(functionNameValue, parametersOfFunction);
                        knownFunctionsList.add(functionNameValue);
                        break;

                }

            case "define-struct":
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

    public boolean emptyListCheck(Element element) {
        if (element.getTagName().equals("quote")) {
            NodeList childrenOfdefOrExpr = element.getChildNodes();
            childrenOfdefOrExpr = removeEmptyText(childrenOfdefOrExpr);
            if (childrenOfdefOrExpr.getLength() == 1) {
                Element child = (Element) childrenOfdefOrExpr.item(0);
                String childType = child.getAttribute("type");
                return Objects.equals(childType, "round");
            }
        }
        return false;
    }

}
