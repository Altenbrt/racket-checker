package io.bitbucket.plt.autotutor.racket.functions.booleans;

import io.bitbucket.plt.autotutor.racket.test.Expression;

import java.util.List;

public class BooleanEQ extends Expression {

    @Override
    public String evaluate(Expression e) {
        return evaluate(e.getRest(super.getId()));
    }

    @Override
    public String evaluate(List<Expression> list) {
        for (Expression e : list) {
            if (!Boolean.valueOf(e.evaluate(this)))
                return Boolean.toString(false);
        }
        return Boolean.toString(true);
    }

    @Override
    public String toString() {
        return "BooleanEQ" + "(" + super.getId() + ")";
    }
}
