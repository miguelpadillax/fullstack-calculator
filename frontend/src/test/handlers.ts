import { http, HttpResponse } from 'msw'
import type { HttpHandler } from 'msw'
import type { CalculationRequest, Operation } from '../features/calculator/types/calculator.types'

type BinaryOperation = Exclude<Operation, 'SQRT'>

const CALCULATE_URL = '*/api/v1/calculator/calculate'

const BINARY_OPERATIONS: Record<BinaryOperation, (a: number, b: number) => number> = {
  ADD: (a, b) => a + b,
  SUBTRACT: (a, b) => a - b,
  MULTIPLY: (a, b) => a * b,
  DIVIDE: (a, b) => a / b,
  POWER: (a, b) => a ** b,
  PERCENTAGE: (a, b) => (a * b) / 100,
}

function operationResponse(operation: Operation, result: number) {
  return HttpResponse.json({ operation, result })
}

function problemResponse(status: number, slug: string, title: string, detail: string) {
  return HttpResponse.json(
    {
      type: `https://calculator.mfpe.dev/errors/${slug}`,
      title,
      status,
      detail,
      instance: '/api/v1/calculator/calculate',
    },
    { status, headers: { 'Content-Type': 'application/problem+json' } },
  )
}

export const handlers: HttpHandler[] = [
  http.post(CALCULATE_URL, async ({ request }) => {
    const { operation, operandA, operandB } = (await request.json()) as CalculationRequest

    if (operation === 'SQRT') {
      if (operandA < 0) {
        return problemResponse(
          422,
          'negative-square-root',
          'Negative square root',
          `Cannot take the square root of ${operandA}`,
        )
      }
      return operationResponse(operation, Math.sqrt(operandA))
    }

    if (operandB === undefined) {
      return problemResponse(
        422,
        'invalid-operand',
        'Invalid operand',
        `operandB is required for binary operation ${operation}`,
      )
    }

    if (operation === 'DIVIDE' && operandB === 0) {
      return problemResponse(422, 'division-by-zero', 'Division by zero', `Cannot divide ${operandA} by ${operandB}`)
    }

    return operationResponse(operation, BINARY_OPERATIONS[operation](operandA, operandB))
  }),
]
