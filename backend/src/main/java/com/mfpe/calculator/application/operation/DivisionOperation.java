package com.mfpe.calculator.application.operation;

import com.mfpe.calculator.domain.exception.DivisionByZeroException;
import com.mfpe.calculator.domain.model.Operation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DivisionOperation implements ArithmeticOperation {

    @Override
    public BigDecimal apply(BigDecimal a, BigDecimal b) {
        if (b.signum() == 0) {
            throw new DivisionByZeroException(a, b);
        }
        return a.divide(b, MATH_CONTEXT);
    }

    @Override
    public Operation type() {
        return Operation.DIVIDE;
    }
}
