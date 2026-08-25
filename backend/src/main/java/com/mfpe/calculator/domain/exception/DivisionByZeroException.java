package com.mfpe.calculator.domain.exception;

import java.math.BigDecimal;

public class DivisionByZeroException extends RuntimeException {

    public DivisionByZeroException(BigDecimal dividend, BigDecimal divisor) {
        super("Cannot divide %s by %s".formatted(dividend.toPlainString(), divisor.toPlainString()));
    }
}
