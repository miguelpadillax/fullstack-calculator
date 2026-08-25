package com.mfpe.calculator.application;

import com.mfpe.calculator.application.operation.OperationRegistry;
import com.mfpe.calculator.domain.exception.DivisionByZeroException;
import com.mfpe.calculator.domain.exception.InvalidOperandException;
import com.mfpe.calculator.domain.model.CalculationRequest;
import com.mfpe.calculator.domain.model.Operation;
import com.mfpe.calculator.domain.model.CalculationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.List;

import com.mfpe.calculator.application.operation.AdditionOperation;
import com.mfpe.calculator.application.operation.DivisionOperation;
import com.mfpe.calculator.application.operation.MultiplicationOperation;
import com.mfpe.calculator.application.operation.PercentageOperation;
import com.mfpe.calculator.application.operation.PowerOperation;
import com.mfpe.calculator.application.operation.SquareRootOperation;
import com.mfpe.calculator.application.operation.SubtractionOperation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculatorServiceTest {

    private final CalculatorService service = new CalculatorService(new OperationRegistry(List.of(
            new AdditionOperation(),
            new SubtractionOperation(),
            new MultiplicationOperation(),
            new DivisionOperation(),
            new PowerOperation(),
            new SquareRootOperation(),
            new PercentageOperation())));

    @ParameterizedTest
    @CsvSource({
            "ADD, 2, 3, 5",
            "SUBTRACT, 10, 4, 6",
            "MULTIPLY, 6, 7, 42",
            "DIVIDE, 10, 4, 2.5",
            "POWER, 2, 10, 1024",
            "PERCENTAGE, 15, 200, 30",
            "SQRT, 16, , 4"
    })
    void shouldCalculate(String operationName, String operandA, String operandB, String expected) {
        CalculationRequest request = new CalculationRequest(
                Operation.valueOf(operationName),
                new BigDecimal(operandA),
                operandB == null || operandB.isBlank() ? null : new BigDecimal(operandB));
        assertThat(service.calculate(request).result()).isEqualByComparingTo(expected);
    }

    @Test
    void shouldReturnOperationInResult() {
        CalculationResult result = service.calculate(
                new CalculationRequest(Operation.ADD, new BigDecimal("2"), new BigDecimal("3")));
        assertThat(result.operation()).isEqualTo(Operation.ADD);
    }

    @Test
    void shouldStripTrailingZerosFromResult() {
        CalculationResult result = service.calculate(
                new CalculationRequest(Operation.PERCENTAGE, new BigDecimal("50"), new BigDecimal("400")));
        assertThat(result.result()).isEqualByComparingTo("200");
        assertThat(result.result().scale()).isLessThanOrEqualTo(0);
    }

    @Test
    void shouldRequireOperandA() {
        assertThatThrownBy(() -> service.calculate(new CalculationRequest(Operation.ADD, null, new BigDecimal("3"))))
                .isInstanceOf(InvalidOperandException.class)
                .hasMessage("operandA is required for operation ADD");
    }

    @Test
    void shouldRequireOperandBForBinaryOperation() {
        assertThatThrownBy(() -> service.calculate(new CalculationRequest(Operation.DIVIDE, new BigDecimal("10"), null)))
                .isInstanceOf(InvalidOperandException.class)
                .hasMessage("operandB is required for binary operation DIVIDE");
    }

    @Test
    void shouldRejectOperandBForUnaryOperation() {
        assertThatThrownBy(() -> service.calculate(
                        new CalculationRequest(Operation.SQRT, new BigDecimal("16"), new BigDecimal("2"))))
                .isInstanceOf(InvalidOperandException.class)
                .hasMessage("operandB must not be provided for unary operation SQRT");
    }

    @Test
    void shouldPropagateDomainErrors() {
        assertThatThrownBy(() -> service.calculate(
                        new CalculationRequest(Operation.DIVIDE, new BigDecimal("10"), new BigDecimal("0"))))
                .isInstanceOf(DivisionByZeroException.class)
                .hasMessage("Cannot divide 10 by 0");
    }
}
