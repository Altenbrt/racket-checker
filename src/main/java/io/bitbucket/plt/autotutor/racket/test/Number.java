package io.bitbucket.plt.autotutor.racket.test;

public class Number extends Expression{
    private final int value;

    public Number(int value) {
        this.value = value;
    }

    @Override
    public String evaluate(Expression e) {
        return Integer.toString(value);
    }

    @Override
    public String toString() {
        return "Number" + "(" + super.getId() + ")";
    }
}
