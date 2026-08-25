# 1. Hexagonal architecture scope

## Context

The backend is a stateless arithmetic microservice: it receives an operation and two
operands, computes a result, and returns it. There is no persistence, no external
service calls. The functional requirements are simple, so the
architectural value of this service cannot come from complexity.

The classic failure mode for an exercise like this is either (a) a single controller
doing arithmetic inline, or (b) full hexagonal with four ports and adapters for a
calculator that stores nothing.

## Decision

Apply hexagonal architecture **restricted to what this problem actually has**:

- **Domain** (`domain.model`, `domain.exception`): pure Java. `Operation`, calculation
  records, and domain exceptions. Zero framework imports.
- **Driving port** (`domain.port.in.CalculatorUseCase`): the single entry point to the
  business logic. The REST adapter depends on this interface, never on the service
  implementation.
- **Application** (`application`): `CalculatorService` implements the port; operation
  strategies live here behind an internal interface.
- **Adapters** (`infrastructure`): REST only — controller, DTOs, error translation,
  CORS, OpenAPI.

Deliberately do **not** implement:

- **Driven ports (`port/out`) and their adapters** — there is nothing to drive: no
  repository, no external client. Inventing a `CalculatorRepository` interface for a
  stateless service would be useless.
- **Anti-corruption/mapping layers between application and domain** — the domain
  model is already small enough that DTO→domain mapping in the controller is a single
  constructor call.

## Consequences

**Positive**

- The domain and application layers are testable without Spring: the unit test suite instantiates classes directly and runs in milliseconds.
- The REST adapter is replaceable (gRPC, CLI, message listener) without touching
  business logic — the dependency direction is enforced by the port, not by convention.

**Negative / trade-offs**

- More files than a two-class solution for four operations.
- If persistence is added later (e.g., calculation history), a driven port will be
  needed — the structure anticipates that addition without requiring it now.
