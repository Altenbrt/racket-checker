import io.bitbucket.plt.autotutor.racket.interpret.DrRacketInterpreter;
import io.bitbucket.plt.autotutor.racket.test.BracketType;
import io.bitbucket.plt.autotutor.racket.test.SyntaxChecker;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        SyntaxChecker syntaxChecker = new SyntaxChecker();
        //System.out.println(syntaxChecker.parameterCheck("(define (smh x y) (+ x y))"));

        HashMap<String, String> smh = new HashMap<>();

        System.out.println(smh.get("hallo"));


        /*
        try {
            DrRacketInterpreter inter = new DrRacketInterpreter("(and true false)");
            System.out.println(inter.getXml());


        } catch (Exception e) {
            System.out.println(e);
        }


        /*
        String s = "(/ 9 3)";
        //"(define (double x)\n" +
        //                "(* x 2))\n" +
        //                "(double 21)"

        //(+ 1 (- 2 1))
        //(- 3 2)
        //(+ 1 (+ 1 1) 1 1 (+ 1 (+ 1 1))))
        try {
            DrRacketInterpreter inter = new DrRacketInterpreter(s);
            System.out.println(inter.getXml());


        } catch (Exception e) {
            System.out.println(e);
        }

         */


    }

}
