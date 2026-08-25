package com.mfpe.calculator.domain.model;

public enum Operation {

    ADD(Arity.BINARY),
    SUBTRACT(Arity.BINARY),
    MULTIPLY(Arity.BINARY),
    DIVIDE(Arity.BINARY),
    POWER(Arity.BINARY),
    SQRT(Arity.UNARY),
    PERCENTAGE(Arity.BINARY);

    public enum Arity {
        BINARY,
        UNARY
    }

    private final Arity arity;

    Operation(Arity arity) {
        this.arity = arity;
    }

    public Arity arity() {
        return arity;
    }
}
