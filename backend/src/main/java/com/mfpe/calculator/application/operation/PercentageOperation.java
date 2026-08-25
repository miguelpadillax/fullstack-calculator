package com.mfpe.calculator.application.operation;

import com.mfpe.calculator.domain.model.Operation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PercentageOperation implements ArithmeticOperation {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @Override
    public BigDecimal apply(BigDecimal a, BigDecimal b) {
        return a.multiply(b).divide(ONE_HUNDRED, MATH_CONTEXT);
    }

    @Override
    public Operation type() {
        return Operation.PERCENTAGE;
    }
}
