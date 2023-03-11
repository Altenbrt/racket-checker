import io.bitbucket.plt.autotutor.racket.interpret.DrRacketInterpreter;
import io.bitbucket.plt.autotutor.racket.test.BracketType;
import io.bitbucket.plt.autotutor.racket.test.SyntaxChecker;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        String test = "0+3i";

        SyntaxChecker syntaxChecker = new SyntaxChecker();
        //System.out.println(syntaxChecker.syntaxCheck(test)); System.out.println();


        try {
            DrRacketInterpreter inter = new DrRacketInterpreter(test);
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
