package com.mfpe.calculator.infrastructure.rest.dto;

import com.mfpe.calculator.domain.model.Operation;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CalculationRequestDto(
        @NotNull(message = "is required") Operation operation,
        @NotNull(message = "is required") BigDecimal operandA,
        BigDecimal operandB) {
}
