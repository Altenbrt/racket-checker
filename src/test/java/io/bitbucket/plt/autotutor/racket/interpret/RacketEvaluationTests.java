package io.bitbucket.plt.autotutor.racket.interpret;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Test
    void testMultipleExpressions() throws Exception {
        //String rktFile = IOUtils.toString(ClassLoader.getSystemResourceAsStream("Demo.rkt"), Charset.defaultCharset());

        String s = "(+ 1 1)";
        List answer = Arrays.stream("2".split("[ ]"))
                .map(x -> Float.valueOf(x))
                .map(x -> x.toString())
                .collect(Collectors.toList());
        DrRacketInterpreter inter = new DrRacketInterpreter(s);
        assertEquals(answer, inter.getAllExpressionEvaluations());

        s = "(+ 1 1) (+ 1 1)";
        answer = Arrays.stream("2 2".split("[ ]"))
                .map(x -> Float.valueOf(x))
                .map(x -> x.toString())
                .collect(Collectors.toList());
        inter = new DrRacketInterpreter(s);
        assertEquals(answer, inter.getAllExpressionEvaluations());

        s = "(+ 1 1) (+ 1 1) \n(+ (+ 2 2) (+ 1 (+ 3 2)))";
        answer = Arrays.stream("2 2 10".split("[ ]"))
                .map(x -> Float.valueOf(x))
                .map(x -> x.toString())
                .collect(Collectors.toList());
        inter = new DrRacketInterpreter(s);
        assertEquals(answer, inter.getAllExpressionEvaluations());
    }

    @Test
    void testLessThan() throws Exception {
        //String rktFile = IOUtils.toString(ClassLoader.getSystemResourceAsStream("Demo.rkt"), Charset.defaultCharset());

        String s = "(< 1 20)";
        DrRacketInterpreter inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());

        s = "(< 2 3 4 5)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());

        s = "(< 1  1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(false), inter.evaluateExpressions());

        s = "(< 2 1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(false), inter.evaluateExpressions());

        s = "(< 2 3 6 5)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(false), inter.evaluateExpressions());

        s = "(< 2 3 1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(false), inter.evaluateExpressions());

        s = "(< -2 -1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());
    }

    @Test
    void testGreaterThan() throws Exception {
        //String rktFile = IOUtils.toString(ClassLoader.getSystemResourceAsStream("Demo.rkt"), Charset.defaultCharset());

        String s = "(> 1 20)";
        DrRacketInterpreter inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(false), inter.evaluateExpressions());

        s = "(> 2 3 4 5)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(false), inter.evaluateExpressions());

        s = "(> 1  1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(false), inter.evaluateExpressions());

        s = "(> 2 1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());

        s = "(> 30 2 1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());

        s = "(> 0 -3)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());

        s = "(> 20 -1 -3 -10)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());
    }

    @Test
    void equal() throws Exception {
        //String rktFile = IOUtils.toString(ClassLoader.getSystemResourceAsStream("Demo.rkt"), Charset.defaultCharset());

        String s = "(= 1 1)";
        DrRacketInterpreter inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());

        s = "(= 1 1 1 1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());

        s = "(= 1  2)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(false), inter.evaluateExpressions());

        s = "(= 1  2 1 1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(false), inter.evaluateExpressions());

        s = "(= 2 2 2 1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(false), inter.evaluateExpressions());

        s = "(= -1 -1 -1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());
    }

    @Test
    void testLessOrEqualThan() throws Exception {
        //String rktFile = IOUtils.toString(ClassLoader.getSystemResourceAsStream("Demo.rkt"), Charset.defaultCharset());

        String s = "(<= 1 20)";
        DrRacketInterpreter inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());

        s = "(<= 2 2 4 5)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());

        s = "(<= 1  1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());

        s = "(<= 2 1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(false), inter.evaluateExpressions());

        s = "(<= 2 3 6 5)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(false), inter.evaluateExpressions());

        s = "(<= 2 3 1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(false), inter.evaluateExpressions());

        s = "(<= -2 -1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());
    }

    @Test
    void testGreaterOrEqualThan() throws Exception {
        //String rktFile = IOUtils.toString(ClassLoader.getSystemResourceAsStream("Demo.rkt"), Charset.defaultCharset());

        String s = "(>= 1 20)";
        DrRacketInterpreter inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(false), inter.evaluateExpressions());

        s = "(>= 2 2 4 5)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(false), inter.evaluateExpressions());

        s = "(>= 1  1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());

        s = "(>= 2 1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());

        s = "(>= 30 2 1)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());

        s = "(>= 0 -3)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());

        s = "(>= 20 -1 -3 -10)";
        inter = new DrRacketInterpreter(s);
        assertEquals(Boolean.toString(true), inter.evaluateExpressions());
    }
}
