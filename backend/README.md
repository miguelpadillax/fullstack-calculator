# Calculator Backend

Arithmetic REST microservice made with Java 25, Spring Boot 4.1 and Maven.

## Run

```bash
./mvnw spring-boot:run
```

## Test & coverage

```bash
./mvnw verify
```

Runs tests (unit + `@WebMvcTest`) and enforces a JaCoCo bundle minimum of 80%
instruction, line, and branch coverage — the build fails below it. Report: `target/site/jacoco/index.html`.

## API

### `POST /api/v1/calculator/calculate`

| Field | Type | Required | Notes |
|---|---|---|---|
| `operation` | enum | yes | `ADD`, `SUBTRACT`, `MULTIPLY`, `DIVIDE`, `POWER`, `SQRT`, `PERCENTAGE` |
| `operandA` | number | yes | |
| `operandB` | number | only for binary ops | must be omitted for `SQRT` |

Success:

```bash
curl -X POST http://localhost:8080/api/v1/calculator/calculate \
  -H "Content-Type: application/json" \
  -d '{"operation":"ADD","operandA":0.1,"operandB":0.2}'
```

```json
{"operation":"ADD","result":0.3}
```

Errors are returned as RFC 7807 `application/problem+json`:

```bash
curl -X POST http://localhost:8080/api/v1/calculator/calculate \
  -H "Content-Type: application/json" \
  -d '{"operation":"DIVIDE","operandA":10,"operandB":0}'
```

```json
{
  "type": "https://calculator.mfpe.dev/errors/division-by-zero",
  "title": "Division by zero",
  "status": 422,
  "detail": "Cannot divide 10 by 0",
  "instance": "/api/v1/calculator/calculate"
}
```

| Case | Status |
|---|---|
| Division by zero | 422 |
| Square root of a negative number | 422 |
| Non-integer exponent (`POWER`) | 422 |
| `operandB` missing in binary op / present in `SQRT` | 422 |
| Unknown `operation` value, missing required field, malformed JSON | 400 |
| Unknown path | 404 |

### Other endpoints

- `GET /swagger-ui.html` — OpenAPI docs
- `GET /actuator/health` — health check

## Configuration

| Property | Default | Description |
|---|---|---|
| `calculator.cors.allowed-origins` | `http://localhost:5173` | Comma-separated allowed origins for `/api/**` |

## Architecture

Hexagonal, with a driving port (`CalculatorUseCase`) with
one REST adapter, pure domain and application layers, and no driven ports because there
is nothing to persist or call. Operations are Strategy classes resolved through an
`OperationRegistry` (a new operation is one enum constant + one class).

Decisions and trade-offs are documented as ADRs in `docs/adr/`.

```
request flow:

REST adapter ──> CalculatorUseCase (port in) ──> CalculatorService ──> OperationRegistry ──> Operation strategy
     │                                                                            
     └── GlobalExceptionHandler ──> ProblemDetail (RFC 7807)
```
