package com.mfpe.calculator.application.operation;

import com.mfpe.calculator.domain.exception.NegativeSquareRootException;
import com.mfpe.calculator.domain.model.Operation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SquareRootOperationTest {

    private final SquareRootOperation operation = new SquareRootOperation();

    @ParameterizedTest
    @CsvSource({
            "16, 4",
            "25.0, 5",
            "2, 1.414213562",
            "0, 0",
            "0.25, 0.5"
    })
    void shouldTakeSquareRoot(String operand, String expected) {
        assertThat(operation.apply(new BigDecimal(operand), null))
                .isEqualByComparingTo(expected);
    }

    @Test
    void shouldRejectNegativeOperand() {
        assertThatThrownBy(() -> operation.apply(new BigDecimal("-4"), null))
                .isInstanceOf(NegativeSquareRootException.class)
                .hasMessage("Cannot take the square root of -4");
    }

    @Test
    void shouldReportType() {
        assertThat(operation.type()).isEqualTo(Operation.SQRT);
    }
}
