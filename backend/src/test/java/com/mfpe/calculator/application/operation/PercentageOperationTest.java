package com.mfpe.calculator.application.operation;

import com.mfpe.calculator.domain.model.Operation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PercentageOperationTest {

    private final PercentageOperation operation = new PercentageOperation();

    @ParameterizedTest
    @CsvSource({
            "15, 200, 30",
            "50, 7, 3.5",
            "0, 100, 0",
            "-10, 200, -20",
            "12.5, 400, 50"
    })
    void shouldComputePercentageOf(String percentage, String value, String expected) {
        assertThat(operation.apply(new BigDecimal(percentage), new BigDecimal(value)))
                .isEqualByComparingTo(expected);
    }

    @Test
    void shouldReportType() {
        assertThat(operation.type()).isEqualTo(Operation.PERCENTAGE);
    }
}
