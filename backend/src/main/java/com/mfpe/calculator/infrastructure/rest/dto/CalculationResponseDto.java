package com.mfpe.calculator.infrastructure.rest.dto;

import com.mfpe.calculator.domain.model.Operation;

import java.math.BigDecimal;

public record CalculationResponseDto(Operation operation, BigDecimal result) {
}
