package com.mfpe.calculator.application.operation;

import com.mfpe.calculator.domain.model.Operation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AdditionOperationTest {

    private final AdditionOperation operation = new AdditionOperation();

    @ParameterizedTest
    @CsvSource({
            "2, 3, 5",
            "0.1, 0.2, 0.3",
            "-5, 5, 0",
            "-2.5, -2.5, -5",
            "0, 0, 0"
    })
    void shouldAdd(String a, String b, String expected) {
        assertThat(operation.apply(new BigDecimal(a), new BigDecimal(b)))
                .isEqualByComparingTo(expected);
    }

    @Test
    void shouldReportType() {
        assertThat(operation.type()).isEqualTo(Operation.ADD);
    }
}
