package com.mfpe.calculator.infrastructure.rest;

import com.mfpe.calculator.domain.exception.DivisionByZeroException;
import com.mfpe.calculator.domain.exception.InvalidOperandException;
import com.mfpe.calculator.domain.model.CalculationResult;
import com.mfpe.calculator.domain.model.Operation;
import com.mfpe.calculator.domain.port.in.CalculatorUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalculatorUseCase calculatorUseCase;

    @Test
    void shouldReturnResultAsJson() throws Exception {
        when(calculatorUseCase.calculate(any()))
                .thenReturn(new CalculationResult(Operation.ADD, new BigDecimal("5")));

        mockMvc.perform(post("/api/v1/calculator/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operation\":\"ADD\",\"operandA\":2,\"operandB\":3}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.operation").value("ADD"))
                .andExpect(jsonPath("$.result").value(5));
    }

    @Test
    void shouldReturnProblemDetailOnDivisionByZero() throws Exception {
        when(calculatorUseCase.calculate(any()))
                .thenThrow(new DivisionByZeroException(new BigDecimal("10"), new BigDecimal("0")));

        mockMvc.perform(post("/api/v1/calculator/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operation\":\"DIVIDE\",\"operandA\":10,\"operandB\":0}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Division by zero"))
                .andExpect(jsonPath("$.detail").value("Cannot divide 10 by 0"))
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void shouldReturnProblemDetailWhenOperandBIsMissingForBinaryOperation() throws Exception {
        when(calculatorUseCase.calculate(any()))
                .thenThrow(new InvalidOperandException("operandB is required for binary operation DIVIDE"));

        mockMvc.perform(post("/api/v1/calculator/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operation\":\"DIVIDE\",\"operandA\":10}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Invalid operand"))
                .andExpect(jsonPath("$.detail").value("operandB is required for binary operation DIVIDE"));
    }

    @Test
    void shouldReturnValidationErrorsWhenOperandAIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/calculator/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operation\":\"ADD\",\"operandB\":3}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.errors", hasItem("operandA: is required")));
    }

    @Test
    void shouldReturnBadRequestForUnknownOperationValue() throws Exception {
        mockMvc.perform(post("/api/v1/calculator/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operation\":\"MOD\",\"operandA\":1,\"operandB\":2}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Malformed request body"));
    }

    @Test
    void shouldReturnProblemDetailForUnknownPath() throws Exception {
        mockMvc.perform(get("/api/v1/calculator/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void shouldAllowCrossOriginRequestsFromConfiguredOrigin() throws Exception {
        when(calculatorUseCase.calculate(any()))
                .thenReturn(new CalculationResult(Operation.ADD, new BigDecimal("5")));

        mockMvc.perform(post("/api/v1/calculator/calculate")
                        .header("Origin", "http://localhost:5173")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operation\":\"ADD\",\"operandA\":2,\"operandB\":3}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }
}
