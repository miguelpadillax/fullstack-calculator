package com.mfpe.calculator.application.operation;

import com.mfpe.calculator.domain.exception.DivisionByZeroException;
import com.mfpe.calculator.domain.exception.InvalidOperandException;
import com.mfpe.calculator.domain.model.Operation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PowerOperation implements ArithmeticOperation {

    @Override
    public BigDecimal apply(BigDecimal a, BigDecimal b) {
        BigDecimal exponent = b.stripTrailingZeros();
        if (exponent.scale() > 0) {
            throw new InvalidOperandException("Exponent must be an integer but was: " + b.toPlainString());
        }
        try {
            int n = exponent.intValueExact();
            if (n >= 0) {
                return a.pow(n);
            }
            // cases where n is negative
            if (a.signum() == 0) {
                throw new DivisionByZeroException(BigDecimal.ONE, a);
            }
            return BigDecimal.ONE.divide(a.pow(-n), MATH_CONTEXT);
        } catch (ArithmeticException e) {
            throw new InvalidOperandException("Exponent out of supported range: " + b.toPlainString());
        }
    }

    @Override
    public Operation type() {
        return Operation.POWER;
    }
}
