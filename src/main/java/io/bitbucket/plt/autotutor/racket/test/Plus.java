package io.bitbucket.plt.autotutor.racket.test;

import java.util.List;

public class Plus extends Expression{

    @Override
    public String evaluate(Expression e) {
        return evaluate(e.getRest(super.getId()));
        //return evaluate(e.getNext(id), e.getNext(id+1));
    }

    @Override
    public String evaluate(List<Expression> list) {
        float result = 0;
        for (Expression e : list) {
            result += Float.valueOf(e.evaluate(this));
        }
        return Float.toString(result);
    }

    @Override
    public String toString() {
        return "Plus" + "(" + super.getId() + ")";
    }
}
