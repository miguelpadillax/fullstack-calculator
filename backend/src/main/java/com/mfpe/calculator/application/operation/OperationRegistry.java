package com.mfpe.calculator.application.operation;

import com.mfpe.calculator.domain.exception.UnsupportedOperationTypeException;
import com.mfpe.calculator.domain.model.Operation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OperationRegistry {

    private final Map<Operation, ArithmeticOperation> strategies;

    public OperationRegistry(List<ArithmeticOperation> operations) {
        this.strategies = operations.stream()
                .collect(Collectors.toUnmodifiableMap(ArithmeticOperation::type, Function.identity()));
    }

    public ArithmeticOperation resolve(Operation operation) {
        return Optional.ofNullable(strategies.get(operation))
                .orElseThrow(() -> new UnsupportedOperationTypeException(operation));
    }
}
