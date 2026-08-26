# AI usage — prompts

## Backend

1. > Following the test structure I created for AdditionOperationTest write the tests for the remaining arithmetic operations. Run ./mvnw verify at the end and report failures. Do not modify src code to make tests pass.

2. > Write a @WebMvcTest for CalculatorController mocking the CalculatorUseCase port. Cover: 200, 422 and 400 http codes. Don't modify the controller.

3. > Given the GlobalExceptionHandler, what HTTP error-handling edge cases am I missing?

4. > Generate a multi-stage Dockerfile for the Java service. Make sure the build stage runs mvn verify and the runtime stage uses a jre-alpine image with layered jar extraction.

5. > Improve the prose and ordering of the ADRs I created in the docs/adr/ folder and suggest clarity  improvements without changing the technical content.

## Frontend

1. > Following the structure of my App.test.tsx, add tests for: division by zero showing the backend problem detail, local validation without an API call, and the Escape shortcut clearing inputs and result.

2. > Generate an nginx.conf that serves a Vite SPA build and proxies /api/ to the backend service. Also a docker-compose with healthchecks.
