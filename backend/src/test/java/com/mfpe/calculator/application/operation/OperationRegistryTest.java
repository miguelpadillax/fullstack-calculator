package com.mfpe.calculator.application.operation;

import com.mfpe.calculator.domain.exception.UnsupportedOperationTypeException;
import com.mfpe.calculator.domain.model.Operation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OperationRegistryTest {

    private static List<ArithmeticOperation> allOperations() {
        return List.of(
                new AdditionOperation(),
                new SubtractionOperation(),
                new MultiplicationOperation(),
                new DivisionOperation(),
                new PowerOperation(),
                new SquareRootOperation(),
                new PercentageOperation());
    }

    private final OperationRegistry registry = new OperationRegistry(allOperations());

    @Test
    void shouldResolveEveryOperation() {
        for (Operation operation : Operation.values()) {
            assertThat(registry.resolve(operation)).isNotNull();
        }
    }

    @Test
    void shouldResolveExactStrategyType() {
        assertThat(registry.resolve(Operation.SQRT)).isInstanceOf(SquareRootOperation.class);
    }

    @Test
    void shouldRejectUnsupportedOperation() {
        OperationRegistry emptyRegistry = new OperationRegistry(List.of());
        assertThatThrownBy(() -> emptyRegistry.resolve(Operation.ADD))
                .isInstanceOf(UnsupportedOperationTypeException.class)
                .hasMessage("Unsupported operation type: ADD");
    }

    @Test
    void shouldFailFastOnDuplicateStrategies() {
        assertThatThrownBy(() -> new OperationRegistry(List.of(new AdditionOperation(), new AdditionOperation())))
                .isInstanceOf(IllegalStateException.class);
    }
}
