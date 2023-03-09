package io.bitbucket.plt.autotutor.racket.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SyntaxCheckerTest {

    SyntaxChecker syntaxChecker = new SyntaxChecker();

    @Test
    void bracketCheck() {
        assertEquals(0, syntaxChecker.bracketCheck("")[0]);
        assertEquals(0, syntaxChecker.bracketCheck("1")[0]);
        assertEquals(0, syntaxChecker.bracketCheck("(+ 1 (+ 1 1))")[0]);
        assertEquals(1, syntaxChecker.bracketCheck("(+ 1 (+ 1 1)")[0]);
        assertEquals(-1, syntaxChecker.bracketCheck("+ 1 (+ 1 1))")[0]);
        assertEquals(0, syntaxChecker.bracketCheck("\"(+ 1 1)\"")[0]);
        assertEquals(1, syntaxChecker.bracketCheck("(+ 1 \"1)()()()()))))(((")[0]);

        assertEquals(0, syntaxChecker.bracketCheck("[]")[1]);
        assertEquals(1, syntaxChecker.bracketCheck("[")[1]);
        assertEquals(-1, syntaxChecker.bracketCheck("]")[1]);
        assertEquals(1, syntaxChecker.bracketCheck("[[]")[1]);

        assertEquals(0, syntaxChecker.bracketCheck("([{<>}]) ([>)")[0]);
        assertEquals(1, syntaxChecker.bracketCheck("([{<>}]) ([>)")[1]);
        assertEquals(0, syntaxChecker.bracketCheck("([{<>}]) ([>)")[2]);
        assertEquals(-1, syntaxChecker.bracketCheck("([{<>}]) ([>)")[3]);
    }

    @Test
    void countQuotationsMarks() {
        assertFalse(syntaxChecker.countQuotationsMarks(""));
        assertFalse(syntaxChecker.countQuotationsMarks("\"test\""));
        assertTrue(syntaxChecker.countQuotationsMarks("te\"st"));
        assertTrue(syntaxChecker.countQuotationsMarks("\"test\"\""));
    }

    @Test
    void syntaxCheck() {
        assertEquals("", syntaxChecker.syntaxCheck(""));
        assertEquals("", syntaxChecker.syntaxCheck("(+ 1 1)"));

        assertEquals("", syntaxChecker.syntaxCheck("(+ 1 1 1 1 1 1 1 111 1 1 1  1 111)"));
        assertEquals("+: expects a Number, given true", syntaxChecker.syntaxCheck("(+ 1 1 1 1 1 1 1 111 1 1 1  1 true)"));

        assertEquals("+: expects a Number, given true", syntaxChecker.syntaxCheck("(+ 1 1) (+ true \"hallo\")"));

        assertEquals("+: expects a Number, given Boolean", syntaxChecker.syntaxCheck("(+ (< 1 1) 1"));
        assertEquals("+: expects a Number, given true", syntaxChecker.syntaxCheck("(+ (+ true 1) 1"));
        assertEquals("+: expects a Number, given Boolean", syntaxChecker.syntaxCheck("(+ (+ (> 1 1 1 1 1 1 1) 1) 1"));

        assertEquals("+: expects 2 argument, but found 1", syntaxChecker.syntaxCheck("(+ 1)"));

        assertEquals("", syntaxChecker.syntaxCheck("(abs 1)"));
        assertEquals("abs: expects 1 argument, but found 0", syntaxChecker.syntaxCheck("(abs )"));
        assertEquals("abs: expects 1 argument, but found 2", syntaxChecker.syntaxCheck("(abs 1 1)"));

        assertEquals("hallo: this function is not defined", syntaxChecker.syntaxCheck("(hallo 1 1)"));

        assertEquals("", syntaxChecker.syntaxCheck("1"));
        assertEquals("", syntaxChecker.syntaxCheck("\"hallo\" 1 true"));
        assertEquals("hallo: this variable is not defined", syntaxChecker.syntaxCheck("hallo"));
        assertEquals("hallo: this variable is not defined", syntaxChecker.syntaxCheck("(+ hallo 1)"));
        assertEquals("+: expects a function call, but there is no open paranthesis before this function", syntaxChecker.syntaxCheck("+ hallo 1"));
    }
}