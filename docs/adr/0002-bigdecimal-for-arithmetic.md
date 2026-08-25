# 2. BigDecimal for arithmetic

## Context

Binary floating point cannot represent most decimal fractions. For a calculator API, returning a "wrong" number
that looks right is the worst possible failure mode: it erodes trust and it surfaces
far from the code that caused it. The classic demo of this bug is literally a
calculator, which makes precision a first-class requirement here rather than a
nicety.

Java's `BigDecimal` provides arbitrary-precision decimal arithmetic with explicit
rounding control.

## Decision

- **All operands and results are `BigDecimal`** end to end: JSON payloads, domain
  records, and operation implementations. No `double` anywhere in the calculation path.
- **A single shared `MathContext(10, HALF_UP)`** (declared on `ArithmeticOperation`)
  is used by every non-terminating operation: division, square root, and reciprocal
  powers. Exact operations (add, subtract, multiply, non-negative integer power) do
  not round at all.
- **Exponentiation accepts integer exponents only.** `BigDecimal.pow(int)` is the only
  exact power in the JDK; using `Math.pow` (double) for fractional exponents would
  silently reintroduce the binary floating point error this ADR exists to avoid.
  Fractional exponents are rejected with a `422` (explicit invalid operand) rather
  than answered inexactly. Negative exponents are computed as the reciprocal
  `1 / a^|n|` under the shared `MathContext`.
- **Results are normalized before serialization**: trailing zeros are stripped
  (`2.50 + 2.50 = 5`, not `5.00`) and negative scales are promoted to scale 0
  (`100`, not `1E+2`).

## Consequences

**Positive**

- `0.1 + 0.2` returns exactly `0.3`; division semantics are explicit and documented
  (10 significant digits, HALF_UP) instead of platform-dependent.
- Error behavior for mathematically unsupported inputs (fractional exponent) is a
  clear client error.

**Negative / trade-offs**

- Non-terminating results are truncated at 10 significant digits. This is a visible
  contract choice: clients see `0.3333333333` for `1/3`. The precision is a constant
  away if requirements change.
- `BigDecimal` is slower than primitives and has API friction (`signum()`,
  `stripTrailingZeros()`, `intValueExact()`). At calculator traffic levels the
  performance cost is irrelevant; the friction is confined to the strategy classes.
