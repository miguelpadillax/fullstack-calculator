package com.mfpe.calculator.application.operation;

import com.mfpe.calculator.domain.exception.DivisionByZeroException;
import com.mfpe.calculator.domain.model.Operation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DivisionOperationTest {

    private final DivisionOperation operation = new DivisionOperation();

    @ParameterizedTest
    @CsvSource({
            "10, 2, 5",
            "10, 3, 3.333333333",
            "1, 3, 0.3333333333",
            "-10, 4, -2.5",
            "7, -2, -3.5",
            "0, 5, 0"
    })
    void shouldDivide(String a, String b, String expected) {
        assertThat(operation.apply(new BigDecimal(a), new BigDecimal(b)))
                .isEqualByComparingTo(expected);
    }

    @Test
    void shouldRejectDivisionByZero() {
        assertThatThrownBy(() -> operation.apply(new BigDecimal("10"), new BigDecimal("0")))
                .isInstanceOf(DivisionByZeroException.class)
                .hasMessage("Cannot divide 10 by 0");
    }

    @Test
    void shouldRejectDivisionOfZeroByZero() {
        assertThatThrownBy(() -> operation.apply(new BigDecimal("0"), new BigDecimal("0.00")))
                .isInstanceOf(DivisionByZeroException.class)
                .hasMessage("Cannot divide 0 by 0.00");
    }

    @Test
    void shouldReportType() {
        assertThat(operation.type()).isEqualTo(Operation.DIVIDE);
    }
}
