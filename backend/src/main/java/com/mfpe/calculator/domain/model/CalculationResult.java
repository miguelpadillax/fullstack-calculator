package com.mfpe.calculator.domain.model;

import java.math.BigDecimal;

public record CalculationResult(Operation operation, BigDecimal result) {
}
