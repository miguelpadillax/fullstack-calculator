package com.mfpe.calculator.domain.exception;

public class InvalidOperandException extends RuntimeException {

    public InvalidOperandException(String message) {
        super(message);
    }
}
