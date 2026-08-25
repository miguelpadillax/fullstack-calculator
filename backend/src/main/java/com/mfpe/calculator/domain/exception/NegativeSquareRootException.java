package com.mfpe.calculator.domain.exception;

import java.math.BigDecimal;

public class NegativeSquareRootException extends RuntimeException {

    public NegativeSquareRootException(BigDecimal operand) {
        super("Cannot take the square root of %s".formatted(operand.toPlainString()));
    }
}
