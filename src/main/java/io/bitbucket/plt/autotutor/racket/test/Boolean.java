package io.bitbucket.plt.autotutor.racket.test;

public class Boolean extends Expression{
    private final java.lang.Boolean value;

    public Boolean(java.lang.Boolean value) {
        this.value = value;
    }

    @Override
    public String evaluate(Expression e) {
        return java.lang.Boolean.toString(value);
    }

    @Override
    public String toString() {
        return "Boolean " + value + " (" + super.getId() + ")";
    }
}
