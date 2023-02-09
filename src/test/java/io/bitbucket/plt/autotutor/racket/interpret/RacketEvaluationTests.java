package io.bitbucket.plt.autotutor.racket.interpret;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RacketEvaluationTests {

    @Test
    void testMultiplication() throws Exception {
        //String rktFile = IOUtils.toString(ClassLoader.getSystemResourceAsStream("Demo.rkt"), Charset.defaultCharset());

        String s = "(* 1 1)";
        DrRacketInterpreter inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(1), inter.evaluateExpressions());

        s = "(* 0 1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(0), inter.evaluateExpressions());

        s = "(* 1 (* 2 1))";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(2), inter.evaluateExpressions());

        s = "(* 1 (* 2 1) 1 (* 0 5) 0)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(0), inter.evaluateExpressions());

        s = "(* (* 2 2) (* 1 (* 3 2)))";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(24), inter.evaluateExpressions());

        s = "(* 0.5 0.25)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString((float)0.125), inter.evaluateExpressions());

        s = "(* -2 -1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(2), inter.evaluateExpressions());

        s = "(* -2 -1 -4)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(-8), inter.evaluateExpressions());
    }

    @Test
    void testMinus() throws Exception {
        //String rktFile = IOUtils.toString(ClassLoader.getSystemResourceAsStream("Demo.rkt"), Charset.defaultCharset());

        String s = "(- 1 1)";
        DrRacketInterpreter inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(0), inter.evaluateExpressions());

        s = "(- 2 (- 2 1))";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(1), inter.evaluateExpressions());

        s = "(- 1 (- 2 1) 1 (- 0 5))";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(4), inter.evaluateExpressions());

        s = "(- (- 2 2) (- 1 (- 3 2)))";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(0), inter.evaluateExpressions());
    }

    @Test
    void testPlus() throws Exception {
        //String rktFile = IOUtils.toString(ClassLoader.getSystemResourceAsStream("Demo.rkt"), Charset.defaultCharset());

        String s = "(+ 1 1)";
        DrRacketInterpreter inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(2), inter.evaluateExpressions());

        s = "(+ 1 (+ 2 1))";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(4), inter.evaluateExpressions());

        s = "(+ 1 (+ 2 1) 1 (+ 0 5))";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(10), inter.evaluateExpressions());

        s = "(+ (+ 2 2) (+ 1 (+ 3 2)))";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(10), inter.evaluateExpressions());

        s = "(+ 0.5 0.25)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString((float)0.75), inter.evaluateExpressions());

        s = "(+ -2 -1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(-3), inter.evaluateExpressions());
    }

    @Test
    void testDivision() throws Exception {
        //String rktFile = IOUtils.toString(ClassLoader.getSystemResourceAsStream("Demo.rkt"), Charset.defaultCharset());

        String s = "(/ 1 1)";
        DrRacketInterpreter inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(1), inter.evaluateExpressions());

        s = "(/ 0 1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(0), inter.evaluateExpressions());

        s = "(/ 1 (/ 2 1))";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString((float)0.5), inter.evaluateExpressions());

        s = "(/ 1 (/ 2 1) 1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString((float)0.5), inter.evaluateExpressions());

        s = "(/ 0.5 0.25)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(2), inter.evaluateExpressions());

        s = "(/ -2 -1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString(2), inter.evaluateExpressions());

        s = "(/ -2 -1 -4)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Float.toString((float)-0.5), inter.evaluateExpressions());
    }
}
