package io.bitbucket.plt.autotutor.racket.functions.numbers;

import io.bitbucket.plt.autotutor.racket.test.Expression;

import java.util.List;

public class Log extends Expression {

    @Override
    public String evaluate(Expression e) {
        return evaluate(e.getRest(super.getId()));
        //return evaluate(e.getNext(id), e.getNext(id+1));
    }

    @Override
    public String evaluate(List<Expression> list) {
        float value = Float.valueOf(list.get(0).evaluate(this));
        return Float.toString((float)Math.log(value));
    }

    @Override
    public String toString() {
        return "Log" + "(" + super.getId() + ")";
    }
}
