package io.bitbucket.plt.autotutor.racket.test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

public class SyntaxChecker {
    ArrayList<String> tokens;

    public void check(String rktString) {
        int brackets = bracketCheck(rktString);
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


    public int bracketCheck(String rktString) {
        int count = 0;              // öffnende Klammer + 1, schließende klammer - 1
        int checkFromIndex = 0;
        int openingPosition = 0;
        int closingPosition = 0;
        boolean openQuotationMarks = false;

        while (checkFromIndex<rktString.length() &&
                (rktString.substring(checkFromIndex, rktString.length()).contains("(") ||
                        rktString.substring(checkFromIndex, rktString.length()).contains(")"))) {
            openingPosition = rktString.indexOf("(", checkFromIndex);       // positive zahl oder -1
            closingPosition = rktString.indexOf(")", checkFromIndex);       // positive zahl oder -1

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
                    if (closingPosition >= 0) {     // Es gibt eine schließende Klammer
                        count--;
                        checkFromIndex = closingPosition + 1;
                    }
                } else {
                    if (closingPosition == -1) {
                        count++;
                        checkFromIndex = openingPosition + 1;
                    } else {
                        if (openingPosition < closingPosition) {
                            count++;
                            checkFromIndex = openingPosition + 1;
                        } else {
                            count--;
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
}
