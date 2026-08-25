package com.mfpe.calculator.application.operation;

import com.mfpe.calculator.domain.exception.NegativeSquareRootException;
import com.mfpe.calculator.domain.model.Operation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SquareRootOperation implements ArithmeticOperation {

    @Override
    public BigDecimal apply(BigDecimal a, BigDecimal b) {
        if (a.signum() < 0) {
            throw new NegativeSquareRootException(a);
        }
        return a.sqrt(MATH_CONTEXT);
    }

    @Override
    public Operation type() {
        return Operation.SQRT;
    }
}
