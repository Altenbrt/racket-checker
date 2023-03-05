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
}