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
    void parameterCheck() {
        assertEquals("", syntaxChecker.parameterCheck(""));
        assertEquals("", syntaxChecker.parameterCheck("(+ 1 1)"));
        assertEquals("Function is not defined: UndefinedFunction",
                syntaxChecker.parameterCheck("(UndefinedFunction 1 1)"));

        assertEquals("+: expects a Number, given \"Not a Number\"", syntaxChecker.parameterCheck("(+ \"Not a Number\" 1"));

        assertEquals("abs: expects 1 argument, but found 0", syntaxChecker.parameterCheck("(abs )"));
        assertEquals("abs: expects 1 argument, but found 2", syntaxChecker.parameterCheck("(abs 1 1)"));

        assertEquals("min: expects 1 argument, but found 0", syntaxChecker.parameterCheck("(min )"));

        assertEquals("expt: expects 2 argument, but found 1", syntaxChecker.parameterCheck("(expt 1)"));
        assertEquals("expt: expects 2 argument, but found 3", syntaxChecker.parameterCheck("(expt 1 1 1)"));
    }
}