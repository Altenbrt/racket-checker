import io.bitbucket.plt.autotutor.racket.interpret.DrRacketInterpreter;

public class Main {

    public static void main(String[] args) {
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


    }

}