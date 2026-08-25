package com.mfpe.calculator.domain.exception;

import com.mfpe.calculator.domain.model.Operation;

public class UnsupportedOperationTypeException extends RuntimeException {

    public UnsupportedOperationTypeException(Operation operation) {
        super("Unsupported operation type: %s".formatted(operation));
    }
}
