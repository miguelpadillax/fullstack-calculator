# 3. Strategy pattern for operations

## Context

The service must support seven operations today (ADD, SUBTRACT, MULTIPLY, DIVIDE,
POWER, SQRT, PERCENTAGE) and is explicitly expected to grow. The default dispatch
mechanism — a `switch` in the service method — concentrates every operation's
validation rules and arithmetic in one growing method and requires modifying that
method (violating the open/closed principle) for every new operation.

## Decision

- Each operation is a **strategy**: a class implementing
  `ArithmeticOperation` (`apply(a, b)` + `type()`), annotated `@Component`.
- **`OperationRegistry`** builds an unmodifiable `Map<Operation, ArithmeticOperation>`
  from Spring's injection of `List<ArithmeticOperation>` (all beans implementing the
  interface). Dispatch is a map lookup; `resolve()` translates a missing entry into
  `UnsupportedOperationTypeException`.
- The registry **fails fast at startup** on duplicate registrations
  (`toUnmodifiableMap` throws on duplicate keys): two beans claiming `ADD` abort boot
  instead of corrupting dispatch at runtime.
- **Arity is declared in the `Operation` enum** (`BINARY`/`UNARY`), so operand-count
  validation is one generic check in `CalculatorService` instead of per-operation
  `if`s. SQRT is unary; everything else is binary.

## Consequences

**Positive**

- Adding an operation touches exactly two files: one new enum constant and one new
  strategy class. No existing code is modified — the open/closed principle holds by
  construction, verified by the registry's startup behavior.
- Each strategy is unit-tested in isolation with plain instantiation (no Spring
  context), keeping the majority of the suite fast.
- Per-operation domain rules (division by zero, negative square root, integer-only
  exponents) live next to the arithmetic they guard.

**Negative / trade-offs**

- One class per operation is more files than a `switch`. The cost is constant and
  small; the benefit (isolated testing, no shotgun surgery on new operations) scales
  with the operation set.
