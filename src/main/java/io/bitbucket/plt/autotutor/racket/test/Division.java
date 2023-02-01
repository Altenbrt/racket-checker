package io.bitbucket.plt.autotutor.racket.test;

import java.util.List;

public class Division extends Expression{

    @Override
    public String evaluate(Expression e) {
        return evaluate(e.getRest(super.getId()));
        //return evaluate(e.getNext(id), e.getNext(id+1));
    }

    @Override
    public String evaluate(List<Expression> list) {
        boolean first = true;
        int result = 0;
        for (Expression e : list) {
            if (first) {
                result = Integer.valueOf(e.evaluate(this));
                first = false;
            } else
                result = result / Integer.valueOf(e.evaluate(this));
        }
        return Integer.toString(result);
    }

    @Override
    public String toString() {
        return "Division" + "(" + super.getId() + ")";
    }
}
