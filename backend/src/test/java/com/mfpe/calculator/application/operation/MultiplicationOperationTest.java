package com.mfpe.calculator.application.operation;

import com.mfpe.calculator.domain.model.Operation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MultiplicationOperationTest {

    private final MultiplicationOperation operation = new MultiplicationOperation();

    @ParameterizedTest
    @CsvSource({
            "6, 7, 42",
            "0.1, 0.2, 0.02",
            "-3, 3, -9",
            "0, 100, 0"
    })
    void shouldMultiply(String a, String b, String expected) {
        assertThat(operation.apply(new BigDecimal(a), new BigDecimal(b)))
                .isEqualByComparingTo(expected);
    }

    @Test
    void shouldReportType() {
        assertThat(operation.type()).isEqualTo(Operation.MULTIPLY);
    }
}
