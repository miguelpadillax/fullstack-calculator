import { describe, expect, it } from 'vitest'
import { http, HttpResponse } from 'msw'
import { server } from '../../../test/server'
import { calculate, CalculatorApiError } from './calculatorApi'
import type { CalculateOptions } from './calculatorApi'
import type { CalculationRequest } from '../types/calculator.types'

const CALCULATE_URL = '*/api/v1/calculator/calculate'

async function expectFailure(
  request: CalculationRequest,
  options?: CalculateOptions,
): Promise<CalculatorApiError> {
  try {
    await calculate(request, options)
  } catch (error) {
    if (error instanceof CalculatorApiError) {
      return error
    }
    throw new Error(`Expected CalculatorApiError but got: ${String(error)}`)
  }
  throw new Error('Expected calculate to reject, but it resolved')
}

describe('calculate', () => {
  it('returns the parsed result for a successful calculation', async () => {
    const result = await calculate({ operation: 'DIVIDE', operandA: 10, operandB: 4 })

    expect(result).toEqual({ operation: 'DIVIDE', result: 2.5 })
  })

  it('sends a JSON request with the exact payload', async () => {
    let capturedBody: unknown
    let capturedContentType: string | null = null

    server.use(
      http.post(CALCULATE_URL, async ({ request }) => {
        capturedBody = await request.json()
        capturedContentType = request.headers.get('content-type')
        return HttpResponse.json({ operation: 'ADD', result: 5 })
      }),
    )

    await calculate({ operation: 'ADD', operandA: 2, operandB: 3 })

    expect(capturedBody).toEqual({ operation: 'ADD', operandA: 2, operandB: 3 })
    expect(capturedContentType).toContain('application/json')
  })

  it('omits operandB for unary operations', async () => {
    let capturedBody: unknown

    server.use(
      http.post(CALCULATE_URL, async ({ request }) => {
        capturedBody = await request.json()
        return HttpResponse.json({ operation: 'SQRT', result: 4 })
      }),
    )

    await calculate({ operation: 'SQRT', operandA: 16 })

    expect(capturedBody).toEqual({ operation: 'SQRT', operandA: 16 })
  })

  it('exposes the backend problem detail on error responses', async () => {
    const error = await expectFailure({ operation: 'DIVIDE', operandA: 10, operandB: 0 })

    expect(error).toBeInstanceOf(CalculatorApiError)
    expect(error.status).toBe(422)
    expect(error.title).toBe('Division by zero')
    expect(error.message).toBe('Cannot divide 10 by 0')
  })

  it('exposes field errors from validation responses', async () => {
    server.use(
      http.post(CALCULATE_URL, () =>
        HttpResponse.json(
          {
            type: 'https://calculator.mfpe.dev/errors/validation',
            title: 'Invalid request',
            status: 400,
            detail: 'Request validation failed',
            instance: '/api/v1/calculator/calculate',
            errors: ['operandA: is required'],
          },
          { status: 400, headers: { 'Content-Type': 'application/problem+json' } },
        ),
      ),
    )

    const error = await expectFailure({ operation: 'ADD', operandA: 2, operandB: 3 })

    expect(error.status).toBe(400)
    expect(error.message).toBe('Request validation failed')
    expect(error.fieldErrors).toEqual(['operandA: is required'])
  })

  it('falls back to the title and response status when the problem is incomplete', async () => {
    server.use(
      http.post(CALCULATE_URL, () =>
        HttpResponse.json(
          { type: 'https://calculator.mfpe.dev/errors/invalid-operand', title: 'Invalid operand' },
          { status: 422, headers: { 'Content-Type': 'application/problem+json' } },
        ),
      ),
    )

    const error = await expectFailure({ operation: 'POWER', operandA: 2, operandB: 1.5 })

    expect(error.status).toBe(422)
    expect(error.message).toBe('Invalid operand')
  })

  it('falls back to a generic message when the error body is not a problem document', async () => {
    server.use(
      http.post(CALCULATE_URL, () =>
        HttpResponse.text('<html>Bad Gateway</html>', {
          status: 502,
          headers: { 'Content-Type': 'text/html' },
        }),
      ),
    )

    const error = await expectFailure({ operation: 'ADD', operandA: 2, operandB: 3 })

    expect(error.status).toBe(502)
    expect(error.message).toBe('Request failed with status 502')
  })

  it('wraps network failures in a CalculatorApiError', async () => {
    server.use(http.post(CALCULATE_URL, () => HttpResponse.error()))

    const error = await expectFailure({ operation: 'ADD', operandA: 2, operandB: 3 })

    expect(error.status).toBe(0)
    expect(error.message).toMatch(/unable to reach the calculator service/i)
  })

  it('times out when the service does not respond', async () => {
    server.use(
      http.post(CALCULATE_URL, () => new Promise<Response>(() => {})),
    )

    const error = await expectFailure({ operation: 'ADD', operandA: 2, operandB: 3 }, { timeoutMs: 50 })

    expect(error.status).toBe(0)
    expect(error.message).toMatch(/took too long to respond/i)
  })
})
