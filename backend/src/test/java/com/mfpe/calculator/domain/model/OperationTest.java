package com.mfpe.calculator.domain.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class OperationTest {

    @ParameterizedTest
    @CsvSource({
            "ADD, BINARY",
            "SUBTRACT, BINARY",
            "MULTIPLY, BINARY",
            "DIVIDE, BINARY",
            "POWER, BINARY",
            "PERCENTAGE, BINARY",
            "SQRT, UNARY"
    })
    void shouldExposeArity(Operation operation, Operation.Arity expectedArity) {
        assertThat(operation.arity()).isEqualTo(expectedArity);
    }
}
