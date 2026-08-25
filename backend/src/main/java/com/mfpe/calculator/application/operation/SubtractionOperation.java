package com.mfpe.calculator.application.operation;

import com.mfpe.calculator.domain.model.Operation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SubtractionOperation implements ArithmeticOperation {

    @Override
    public BigDecimal apply(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }

    @Override
    public Operation type() {
        return Operation.SUBTRACT;
    }
}
