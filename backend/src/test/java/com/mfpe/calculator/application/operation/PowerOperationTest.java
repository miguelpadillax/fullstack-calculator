package com.mfpe.calculator.application.operation;

import com.mfpe.calculator.domain.exception.DivisionByZeroException;
import com.mfpe.calculator.domain.exception.InvalidOperandException;
import com.mfpe.calculator.domain.model.Operation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PowerOperationTest {

    private final PowerOperation operation = new PowerOperation();

    @ParameterizedTest
    @CsvSource({
            "2, 10, 1024",
            "5, 0, 1",
            "0, 0, 1",
            "2, -2, 0.25",
            "-2, 3, -8",
            "-2, 2, 4",
            "10, 1, 10",
            "2, 2.0, 4",
            "2, 2.00, 4"
    })
    void shouldRaiseToPower(String base, String exponent, String expected) {
        assertThat(operation.apply(new BigDecimal(base), new BigDecimal(exponent)))
                .isEqualByComparingTo(expected);
    }

    @Test
    void shouldRejectFractionalExponent() {
        assertThatThrownBy(() -> operation.apply(new BigDecimal("2"), new BigDecimal("1.5")))
                .isInstanceOf(InvalidOperandException.class)
                .hasMessage("Exponent must be an integer but was: 1.5");
    }

    @Test
    void shouldRejectZeroBaseWithNegativeExponent() {
        assertThatThrownBy(() -> operation.apply(new BigDecimal("0"), new BigDecimal("-1")))
                .isInstanceOf(DivisionByZeroException.class)
                .hasMessage("Cannot divide 1 by 0");
    }

    @Test
    void shouldRejectExponentOutsideSupportedRange() {
        assertThatThrownBy(() -> operation.apply(new BigDecimal("2"), new BigDecimal("1E+20")))
                .isInstanceOf(InvalidOperandException.class)
                .hasMessage("Exponent out of supported range: 100000000000000000000");
    }

    @Test
    void shouldReportType() {
        assertThat(operation.type()).isEqualTo(Operation.POWER);
    }
}
