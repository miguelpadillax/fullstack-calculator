package com.mfpe.calculator.domain.port.in;

import com.mfpe.calculator.domain.model.CalculationRequest;
import com.mfpe.calculator.domain.model.CalculationResult;

public interface CalculatorUseCase {

    CalculationResult calculate(CalculationRequest request);
}
