package com.mfpe.calculator.application;

import com.mfpe.calculator.application.operation.OperationRegistry;
import com.mfpe.calculator.domain.exception.InvalidOperandException;
import com.mfpe.calculator.domain.model.CalculationRequest;
import com.mfpe.calculator.domain.model.CalculationResult;
import com.mfpe.calculator.domain.port.in.CalculatorUseCase;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CalculatorService implements CalculatorUseCase {

    private final OperationRegistry registry;

    public CalculatorService(OperationRegistry registry) {
        this.registry = registry;
    }

    @Override
    public CalculationResult calculate(CalculationRequest request) {
        validate(request);
        BigDecimal result = registry.resolve(request.operation())
                .apply(request.operandA(), request.operandB());
        return new CalculationResult(request.operation(), result.stripTrailingZeros());
    }

    private void validate(CalculationRequest request) {
        if (request.operandA() == null) {
            throw new InvalidOperandException("operandA is required for operation " + request.operation());
        }
        switch (request.operation().arity()) {
            case BINARY -> {
                if (request.operandB() == null) {
                    throw new InvalidOperandException("operandB is required for binary operation " + request.operation());
                }
            }
            case UNARY -> {
                if (request.operandB() != null) {
                    throw new InvalidOperandException("operandB must not be provided for unary operation " + request.operation());
                }
            }
        }
    }
}
