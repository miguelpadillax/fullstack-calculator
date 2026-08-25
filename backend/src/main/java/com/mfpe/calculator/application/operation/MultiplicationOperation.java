package com.mfpe.calculator.application.operation;

import com.mfpe.calculator.domain.model.Operation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MultiplicationOperation implements ArithmeticOperation {

    @Override
    public BigDecimal apply(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }

    @Override
    public Operation type() {
        return Operation.MULTIPLY;
    }
}
