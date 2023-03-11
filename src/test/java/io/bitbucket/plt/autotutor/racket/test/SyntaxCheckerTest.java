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
    void literalCheck() {
        assertEquals("", syntaxChecker.syntaxCheck("123"));         // Number
        assertEquals("", syntaxChecker.syntaxCheck("3/2 5.5"));
        assertEquals("", syntaxChecker.syntaxCheck("#true"));       // Boolean
        assertEquals("", syntaxChecker.syntaxCheck("#t #f #false"));
        assertEquals("", syntaxChecker.syntaxCheck("'name '+"));    // Symbol
        assertEquals("", syntaxChecker.syntaxCheck("\"abcdef\""));        // String
        assertEquals("", syntaxChecker.syntaxCheck("\"This is a string\" \"This is a string with \\\" inside\""));
        assertEquals("", syntaxChecker.syntaxCheck("#\\a"));         // Character
        assertEquals("", syntaxChecker.syntaxCheck("#\\b #\\space"));
    }

    @Test
    void preDefinedVariablesCheck() {
        assertEquals("", syntaxChecker.syntaxCheck("empty"));
        assertEquals("", syntaxChecker.syntaxCheck("false true empty e pi null"));

        assertEquals("emptyy: this variable is not defined", syntaxChecker.syntaxCheck("emptyy"));
    }

    @Test
    void onNumbersCheck() {
        assertEquals("", syntaxChecker.syntaxCheck("(* 5 3)"));
        assertEquals("", syntaxChecker.syntaxCheck("(* 5 3 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(+ 2/3 1/16)"));
        assertEquals("", syntaxChecker.syntaxCheck("(+ 3 2 5 8)"));
        assertEquals("", syntaxChecker.syntaxCheck("(- 5)"));
        assertEquals("", syntaxChecker.syntaxCheck("(- 5 3)"));
        assertEquals("", syntaxChecker.syntaxCheck("(- 5 3 1)"));
        assertEquals("", syntaxChecker.syntaxCheck("(/ 12 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(/ 12 2 3)"));
        assertEquals("", syntaxChecker.syntaxCheck("(< 42 2/5)"));
        assertEquals("", syntaxChecker.syntaxCheck("(<= 42 2/5)"));
        assertEquals("", syntaxChecker.syntaxCheck("(= 42 2/5)"));
        assertEquals("", syntaxChecker.syntaxCheck("(> 42 2/5)"));
        assertEquals("", syntaxChecker.syntaxCheck("(>= 42 42)"));
        assertEquals("", syntaxChecker.syntaxCheck("(abs -12)"));
        assertEquals("", syntaxChecker.syntaxCheck("(acos 0)"));
        assertEquals("", syntaxChecker.syntaxCheck("(add1 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(angle (make-polar 3 4))"));
        assertEquals("", syntaxChecker.syntaxCheck("(asin 0)"));
        assertEquals("", syntaxChecker.syntaxCheck("(atan 0)"));
        assertEquals("", syntaxChecker.syntaxCheck("(atan 0.5)"));
        assertEquals("", syntaxChecker.syntaxCheck("(atan 3 4)"));
        assertEquals("", syntaxChecker.syntaxCheck("(atan -2 -1)"));
        assertEquals("", syntaxChecker.syntaxCheck("(ceiling 12.3)"));
        assertEquals("", syntaxChecker.syntaxCheck("(complex? 1-2i)"));
        assertEquals("", syntaxChecker.syntaxCheck("(conjugate 3+4i)"));
        assertEquals("", syntaxChecker.syntaxCheck("(conjugate -2-5i)"));
        assertEquals("", syntaxChecker.syntaxCheck("(conjugate (make-polar 3 4))"));
        assertEquals("", syntaxChecker.syntaxCheck("(cos pi)"));
        assertEquals("", syntaxChecker.syntaxCheck("(cosh 10)"));
        assertEquals("", syntaxChecker.syntaxCheck("(current-seconds)"));
        assertEquals("", syntaxChecker.syntaxCheck("(denominator 2/3)"));
        assertEquals("", syntaxChecker.syntaxCheck("(even? 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(exact->inexact 12)"));
        assertEquals("", syntaxChecker.syntaxCheck("(exact? (sqrt 2))"));
        assertEquals("", syntaxChecker.syntaxCheck("(exp -2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(expt 16 1/2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(expt 3 -4)"));
        assertEquals("", syntaxChecker.syntaxCheck("(floor 12.3)"));
        assertEquals("", syntaxChecker.syntaxCheck("(gcd 6 12 8)"));
        assertEquals("", syntaxChecker.syntaxCheck("(imag-part 3+4i)"));
        assertEquals("", syntaxChecker.syntaxCheck("(inexact->exact 12.0)"));
        assertEquals("", syntaxChecker.syntaxCheck("(inexact? 1-2i)"));
        assertEquals("", syntaxChecker.syntaxCheck("(integer->char 42)"));
        assertEquals("", syntaxChecker.syntaxCheck("(integer-sqrt 11)"));
        assertEquals("", syntaxChecker.syntaxCheck("(integer-sqrt -11)"));
        assertEquals("", syntaxChecker.syntaxCheck("(integer? (sqrt 2))"));
        assertEquals("", syntaxChecker.syntaxCheck("(lcm 6 12 8)"));
        assertEquals("", syntaxChecker.syntaxCheck("(log 12)"));
        assertEquals("", syntaxChecker.syntaxCheck("(magnitude (make-polar 3 4))"));
        assertEquals("", syntaxChecker.syntaxCheck("(make-polar 3 4)"));
        assertEquals("", syntaxChecker.syntaxCheck("(make-rectangular 3 4)"));
        assertEquals("", syntaxChecker.syntaxCheck("(max 3 2 8 7 2 9 0)"));
        assertEquals("", syntaxChecker.syntaxCheck("(min 3 2 8 7 2 9 0)"));
        assertEquals("", syntaxChecker.syntaxCheck("(modulo 9 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(modulo 3 -4)"));
        assertEquals("", syntaxChecker.syntaxCheck("(negative? -2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(number->string 42)"));
        assertEquals("", syntaxChecker.syntaxCheck("(number->string-digits 0.9 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(number->string-digits pi 4)"));
        assertEquals("", syntaxChecker.syntaxCheck("(exact? (sqrt 2))"));
        assertEquals("", syntaxChecker.syntaxCheck("(number? \"hello world\")"));
        assertEquals("", syntaxChecker.syntaxCheck("(number? 42)"));
        assertEquals("", syntaxChecker.syntaxCheck("(numerator 2/3)"));
        assertEquals("", syntaxChecker.syntaxCheck("(odd? 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(positive? -2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(quotient 9 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(quotient 3 4)"));
        assertEquals("", syntaxChecker.syntaxCheck("(random 42)"));
        assertEquals("", syntaxChecker.syntaxCheck("(rational? 1)"));
        assertEquals("", syntaxChecker.syntaxCheck("(rational? -2.349)"));
        assertEquals("", syntaxChecker.syntaxCheck("(rational? #i1.23456789)"));
        assertEquals("", syntaxChecker.syntaxCheck("(rational? (sqrt -1))"));
        assertEquals("", syntaxChecker.syntaxCheck("(rational? pi)"));
        assertEquals("", syntaxChecker.syntaxCheck("(rational? e)"));
        assertEquals("", syntaxChecker.syntaxCheck("(rational? 1-2i)"));
        assertEquals("", syntaxChecker.syntaxCheck("(real-part 3+4i)"));
        assertEquals("", syntaxChecker.syntaxCheck("(real? 1-2i)"));
        assertEquals("", syntaxChecker.syntaxCheck("(remainder 9 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(remainder 3 4)"));
        assertEquals("", syntaxChecker.syntaxCheck("(round 12.3)"));
        assertEquals("", syntaxChecker.syntaxCheck("(sgn -12)"));
        assertEquals("", syntaxChecker.syntaxCheck("(sin pi)"));
        assertEquals("", syntaxChecker.syntaxCheck("(sinh 10)"));
        assertEquals("", syntaxChecker.syntaxCheck("(sqr 8)"));
        assertEquals("", syntaxChecker.syntaxCheck("(sqrt 9)"));
        assertEquals("", syntaxChecker.syntaxCheck("(sqrt 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(sub1 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(tan pi)"));
        assertEquals("", syntaxChecker.syntaxCheck("(zero? 2)"));
    }

    @Test
    void onBooleansCheck() {

    }

    @Test
    void onSymbolsCheck() {

    }

    @Test
    void onListsCheck() {

    }

    @Test
    void onPosnsCheck() {

    }

    @Test
    void onCharactersCheck() {

    }

    @Test
    void onStringsCheck() {

    }

    @Test
    void syntaxCheck() {


        assertEquals("", syntaxChecker.syntaxCheck("(add1 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(ceiling 12.3)"));
        assertEquals("", syntaxChecker.syntaxCheck("(even? 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(exp -2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(expt 16 1/2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(expt 3 -4)"));
        assertEquals("", syntaxChecker.syntaxCheck("(floor 12.3)"));
        assertEquals("", syntaxChecker.syntaxCheck("(log 12)"));
        assertEquals("", syntaxChecker.syntaxCheck("(max 3 2 8 7 2 9 0)"));
        assertEquals("", syntaxChecker.syntaxCheck("(min 3 2 8 7 2 9 0)"));
        assertEquals("", syntaxChecker.syntaxCheck("(negative? -2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(odd? 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(positive? -2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(random 42)"));
        assertEquals("", syntaxChecker.syntaxCheck("(round 12.3)"));
        assertEquals("", syntaxChecker.syntaxCheck("(sqr 8)"));
        assertEquals("", syntaxChecker.syntaxCheck("(sqrt 9)"));
        assertEquals("", syntaxChecker.syntaxCheck("(sqrt 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(sub1 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(zero? 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(boolean=? #true #false)"));
        assertEquals("", syntaxChecker.syntaxCheck("(boolean? 42)"));
        assertEquals("", syntaxChecker.syntaxCheck("(boolean? #false)"));
        assertEquals("", syntaxChecker.syntaxCheck("(false? #false)"));
        assertEquals("", syntaxChecker.syntaxCheck("(not #false)"));
        assertEquals("", syntaxChecker.syntaxCheck("(append (cons 1 (cons 2 '())) (cons \"a\" (cons \"b\" empty)))"));
        assertEquals("", syntaxChecker.syntaxCheck("(cons 1 '())"));
        assertEquals("", syntaxChecker.syntaxCheck("(cons 1 (cons 2 '()))"));
        assertEquals("", syntaxChecker.syntaxCheck("(empty? '())"));
        assertEquals("", syntaxChecker.syntaxCheck("(first (list 2 \"hello\" #true))"));
        assertEquals("", syntaxChecker.syntaxCheck("(length (list 2 \"hello\" #true))"));
        assertEquals("", syntaxChecker.syntaxCheck("(list 1 2 3 4 5 6 7 8 9 0)"));
        assertEquals("", syntaxChecker.syntaxCheck("(member \"hello\" (list 2 \"hello\" #true))"));
        assertEquals("", syntaxChecker.syntaxCheck("(range 0 10 2)"));
        assertEquals("", syntaxChecker.syntaxCheck("(remove \"hello\" (list 2 \"hello\" #true))"));
        assertEquals("", syntaxChecker.syntaxCheck("(remove \"hello\" (list \"hello\" #true \"hello\"))"));
        assertEquals("", syntaxChecker.syntaxCheck("(make-posn 3 3)"));
        assertEquals("", syntaxChecker.syntaxCheck("(make-posn \"hello\" #true)"));
        assertEquals("", syntaxChecker.syntaxCheck("(posn-x (make-posn 2 -3))"));
        assertEquals("", syntaxChecker.syntaxCheck("(posn-y (make-posn 2 -3))"));
        assertEquals("", syntaxChecker.syntaxCheck("pi"));
        assertEquals("", syntaxChecker.syntaxCheck("e"));
        assertEquals("", syntaxChecker.syntaxCheck("empty"));
        assertEquals("", syntaxChecker.syntaxCheck("null"));
        assertEquals("", syntaxChecker.syntaxCheck("'()"));


        assertEquals("", syntaxChecker.syntaxCheck(""));

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

        assertEquals("", syntaxChecker.syntaxCheck("pi"));
        assertEquals("", syntaxChecker.syntaxCheck("(+ pi 1)"));
        assertEquals("boolean=?: expects a Boolean, given pi", syntaxChecker.syntaxCheck("(boolean=? pi 1)"));
        assertEquals("", syntaxChecker.syntaxCheck("empty true false pi null"));

        assertEquals("make-posn: expects 2 argument, but found 3", syntaxChecker.syntaxCheck("(make-posn 3 3 3)"));
        assertEquals("make-posn: expects 2 argument, but found 1", syntaxChecker.syntaxCheck("(make-posn \"hello\")"));
        assertEquals("posn-x: expects 1 argument, but found 2", syntaxChecker.syntaxCheck("(posn-x (make-posn 2 -3) 1)"));
        assertEquals("posn-y: expects a Name, given 1", syntaxChecker.syntaxCheck("(posn-y 1)"));

        assertEquals("append: expects a List, given true", syntaxChecker.syntaxCheck("(append (cons 1 (cons 2 '())) '() true)"));
        assertEquals("cons: expects 2 argument, but found 1", syntaxChecker.syntaxCheck("(cons 1)"));
        assertEquals("cons: expects 2 argument, but found 3", syntaxChecker.syntaxCheck("(cons 1 (cons 2 '() '()))"));
        assertEquals("", syntaxChecker.syntaxCheck("(empty? \"hallo\")"));
        assertEquals("first: expects a List, given 1", syntaxChecker.syntaxCheck("(first 1)"));
        assertEquals("length: expects a List, given true", syntaxChecker.syntaxCheck("(length true)"));
        assertEquals("ye: this variable is not defined", syntaxChecker.syntaxCheck("(list 1 \"hallo\" #\\a false pi empty null '() (cons 1 '()) (list 1 2 3) ye)"));
        assertEquals("member: expects a List, given \"peter\"", syntaxChecker.syntaxCheck("(member \"hello\" \"peter\")"));
        assertEquals("range: expects a Number, given #\\c", syntaxChecker.syntaxCheck("(range 0 #\\c 2)"));
        assertEquals("remove: expects a List, given 1", syntaxChecker.syntaxCheck("(remove \"hello\" 1)"));
        assertEquals("remove: expects 2 argument, but found 3", syntaxChecker.syntaxCheck("(remove \"hello\" smh (list \"hello\" #true \"hello\"))"));

        assertEquals("", syntaxChecker.syntaxCheck("(if true true false)"));
        assertEquals("", syntaxChecker.syntaxCheck("(if (< 2 1) pi  (list 1 2 3))"));
        assertEquals("if: expects a Boolean, given Number", syntaxChecker.syntaxCheck("(if (+ 2 1) pi  (list 1 2 3))"));

        assertEquals("", syntaxChecker.syntaxCheck("(cond [true 1])"));
        assertEquals("", syntaxChecker.syntaxCheck("(cond [true 1] [false 2] [else true])"));
        assertEquals("cond: found an else clause that isn't the last clause in its cond expression", syntaxChecker.syntaxCheck("(cond [true 1] [else true] [false 2])"));
        assertEquals("cond: expected a clause with a question and an answer, but found Number", syntaxChecker.syntaxCheck("(cond [true 1] 1 [false 2])"));
        assertEquals("cond: expected a clause with a question and an answer, but found a clause with 3 parts", syntaxChecker.syntaxCheck("(cond [true 1] [false 2] [else true \"hallo\"])"));
        assertEquals("<: expects a Number, given true", syntaxChecker.syntaxCheck("(cond [true 1] [false 2] [else (< 1 true)])"));
        assertEquals("+: expects a Number, given #\\c", syntaxChecker.syntaxCheck("(cond [true 1] [false (+ 1 #\\c)] [else (< 1 2)])"));
        assertEquals("<: expects a Number, given List", syntaxChecker.syntaxCheck("(cond [(< '() 1) 1] [false (+ 1 #\\c)] [else (< 1 2)])"));

    }
}