package io.bitbucket.plt.autotutor.racket.functions.numbers;

import io.bitbucket.plt.autotutor.racket.test.Expression;

import java.util.List;

public class GreaterThan extends Expression {

    @Override
    public String evaluate(Expression e) {
        return evaluate(e.getRest(super.getId()));
        //return evaluate(e.getNext(id), e.getNext(id+1));
    }

    @Override
    public String evaluate(List<Expression> list) {
        Boolean first = true;
        float value = 0;
        for (Expression e : list) {
            float valueNow = Float.valueOf(e.evaluate(this));
            if (first) {
                value = valueNow;
                first = false;
                continue;
            }

            if (!(value > valueNow))
                return Boolean.toString(false);
            value = valueNow;
        }
        return Boolean.toString(true);
    }

    @Override
    public String toString() {
        return "GreaterThan" + "(" + super.getId() + ")";
    }
}
