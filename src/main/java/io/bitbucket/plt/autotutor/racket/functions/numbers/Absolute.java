package io.bitbucket.plt.autotutor.racket.functions.numbers;

import io.bitbucket.plt.autotutor.racket.test.Expression;

import java.util.List;

public class Absolute extends Expression {

    @Override
    public String evaluate(Expression e) {
        return evaluate(e.getRest(super.getId()));
        //return evaluate(e.getNext(id), e.getNext(id+1));
    }

    @Override
    public String evaluate(List<Expression> list) {
        return Float.toString(Math.abs(Float.valueOf(list.get(0).evaluate(this))));
    }

    @Override
    public String toString() {
        return "Absolute" + "(" + super.getId() + ")";
    }
}
