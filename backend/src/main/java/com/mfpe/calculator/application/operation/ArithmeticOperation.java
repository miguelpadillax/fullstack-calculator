package com.mfpe.calculator.application.operation;

import com.mfpe.calculator.domain.model.Operation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public interface ArithmeticOperation {

    MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP);

    BigDecimal apply(BigDecimal a, BigDecimal b);

    Operation type();
}
