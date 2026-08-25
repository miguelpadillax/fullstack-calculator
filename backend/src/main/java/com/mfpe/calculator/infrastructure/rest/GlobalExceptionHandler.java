package com.mfpe.calculator.infrastructure.rest;

import com.mfpe.calculator.domain.exception.DivisionByZeroException;
import com.mfpe.calculator.domain.exception.InvalidOperandException;
import com.mfpe.calculator.domain.exception.NegativeSquareRootException;
import com.mfpe.calculator.domain.exception.UnsupportedOperationTypeException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String ERROR_TYPE_BASE = "https://calculator.mfpe.com/errors/";

    @ExceptionHandler(DivisionByZeroException.class)
    public ProblemDetail handleDivisionByZero(DivisionByZeroException ex, HttpServletRequest request) {
        return problem(HttpStatus.UNPROCESSABLE_CONTENT, "Division by zero", "division-by-zero", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidOperandException.class)
    public ProblemDetail handleInvalidOperand(InvalidOperandException ex, HttpServletRequest request) {
        return problem(HttpStatus.UNPROCESSABLE_CONTENT, "Invalid operand", "invalid-operand", ex.getMessage(), request);
    }

    @ExceptionHandler(NegativeSquareRootException.class)
    public ProblemDetail handleNegativeSquareRoot(NegativeSquareRootException ex, HttpServletRequest request) {
        return problem(HttpStatus.UNPROCESSABLE_CONTENT, "Negative square root", "negative-square-root",
                ex.getMessage(), request);
    }

    @ExceptionHandler(UnsupportedOperationTypeException.class)
    public ProblemDetail handleUnsupportedOperation(UnsupportedOperationTypeException ex, HttpServletRequest request) {
        return problem(HttpStatus.UNPROCESSABLE_CONTENT, "Unsupported operation", "unsupported-operation",
                ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception processing {} {}", request.getMethod(), request.getRequestURI(), ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "internal",
                "An unexpected error occurred. Please try again later.", request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errors = ex.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .sorted()
                .toList();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, "Request validation failed");
        problemDetail.setTitle("Invalid request");
        problemDetail.setType(URI.create(ERROR_TYPE_BASE + "validation"));
        problemDetail.setProperty("errors", errors);
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String detail = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle("Malformed request body");
        problemDetail.setType(URI.create(ERROR_TYPE_BASE + "malformed-body"));
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    private ProblemDetail problem(
            HttpStatus status, String title, String typeSuffix, String detail, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create(ERROR_TYPE_BASE + typeSuffix));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }
}
