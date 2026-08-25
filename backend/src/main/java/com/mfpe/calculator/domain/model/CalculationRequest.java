package com.mfpe.calculator.domain.model;

import java.math.BigDecimal;

public record CalculationRequest(Operation operation, BigDecimal operandA, BigDecimal operandB) {
}
