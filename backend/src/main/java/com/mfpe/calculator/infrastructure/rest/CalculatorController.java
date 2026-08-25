package com.mfpe.calculator.infrastructure.rest;

import com.mfpe.calculator.domain.model.CalculationRequest;
import com.mfpe.calculator.domain.model.CalculationResult;
import com.mfpe.calculator.domain.port.in.CalculatorUseCase;
import com.mfpe.calculator.infrastructure.rest.dto.CalculationRequestDto;
import com.mfpe.calculator.infrastructure.rest.dto.CalculationResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calculator")
@Tag(name = "Calculator", description = "Arithmetic operations")
public class CalculatorController {

    private final CalculatorUseCase calculatorUseCase;

    public CalculatorController(CalculatorUseCase calculatorUseCase) {
        this.calculatorUseCase = calculatorUseCase;
    }

    @PostMapping("/calculate")
    public CalculationResponseDto calculate(@Valid @RequestBody CalculationRequestDto requestDto) {
        CalculationRequest request = new CalculationRequest(
                requestDto.operation(), requestDto.operandA(), requestDto.operandB());
        CalculationResult result = calculatorUseCase.calculate(request);
        return new CalculationResponseDto(result.operation(), result.result());
    }
}
