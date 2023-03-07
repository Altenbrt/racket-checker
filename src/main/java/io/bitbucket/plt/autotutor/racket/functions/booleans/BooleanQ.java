package io.bitbucket.plt.autotutor.racket.functions.booleans;

import io.bitbucket.plt.autotutor.racket.test.Expression;

import java.util.List;

public class BooleanQ extends Expression {

    @Override
    public String evaluate(Expression e) {
        return evaluate(e.getRest(super.getId()));
    }

    @Override
    public String evaluate(List<Expression> list) {
        for (Expression e : list) {
            if (e.evaluate(this).equals("true") || e.evaluate(this).equals("false"))
                return Boolean.toString(true);
        }
        return Boolean.toString(false);
    }

    @Override
    public String toString() {
        return "BooleanQ" + "(" + super.getId() + ")";
    }
}
