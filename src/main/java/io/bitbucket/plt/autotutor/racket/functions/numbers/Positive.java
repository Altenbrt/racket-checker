package io.bitbucket.plt.autotutor.racket.functions.numbers;

import io.bitbucket.plt.autotutor.racket.test.Expression;

import java.util.List;

public class Positive extends Expression {

    @Override
    public String evaluate(Expression e) {
        return evaluate(e.getRest(super.getId()));
        //return evaluate(e.getNext(id), e.getNext(id+1));
    }

    @Override
    public String evaluate(List<Expression> list) {
        int value = (int)(float)Float.valueOf(list.get(0).evaluate(this));    //Because Racket only accepts Integers in even?
        return Boolean.toString(value > 0);
    }

    @Override
    public String toString() {
        return "Positive" + "(" + super.getId() + ")";
    }
}
