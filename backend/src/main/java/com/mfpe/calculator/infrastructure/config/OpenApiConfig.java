package com.mfpe.calculator.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI calculatorOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Calculator API")
                        .description("Arithmetic REST API for the fullstack calculator")
                        .version("1.0.0"));
    }
}
