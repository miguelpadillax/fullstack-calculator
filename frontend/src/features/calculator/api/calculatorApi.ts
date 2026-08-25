import type { CalculationRequest, CalculationResult, ProblemDetail } from '../types/calculator.types'

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/+$/, '')
const CALCULATE_URL = `${API_BASE_URL}/api/v1/calculator/calculate`
const DEFAULT_TIMEOUT_MS = 10_000

export interface CalculateOptions {
  timeoutMs?: number
}

export class CalculatorApiError extends Error {
  readonly status: number
  readonly title?: string
  readonly fieldErrors?: string[]

  private constructor(status: number, message: string, title?: string, fieldErrors?: string[]) {
    super(message)
    this.name = 'CalculatorApiError'
    this.status = status
    this.title = title
    this.fieldErrors = fieldErrors
  }

  static fromProblem(problem: ProblemDetail, fallbackStatus: number): CalculatorApiError {
    const status = problem.status ?? fallbackStatus
    const message = problem.detail ?? problem.title ?? `Request failed with status ${status}`
    return new CalculatorApiError(status, message, problem.title, problem.errors)
  }

  static network(): CalculatorApiError {
    return new CalculatorApiError(
      0,
      'Unable to reach the calculator service. Please check your connection and try again.',
    )
  }

  static timeout(): CalculatorApiError {
    return new CalculatorApiError(0, 'The calculator service took too long to respond. Please try again.')
  }
}

export async function calculate(
  request: CalculationRequest,
  { timeoutMs = DEFAULT_TIMEOUT_MS }: CalculateOptions = {},
): Promise<CalculationResult> {
  const controller = new AbortController()
  const timeoutId = setTimeout(() => controller.abort(), timeoutMs)

  let response: Response
  try {
    response = await fetch(CALCULATE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
      signal: controller.signal,
    })
  } catch (error) {
    if (error instanceof Error && error.name === 'AbortError') {
      throw CalculatorApiError.timeout()
    }
    throw CalculatorApiError.network()
  } finally {
    clearTimeout(timeoutId)
  }

  if (!response.ok) {
    throw CalculatorApiError.fromProblem(await readProblem(response), response.status)
  }

  return (await response.json()) as CalculationResult
}

async function readProblem(response: Response): Promise<ProblemDetail> {
  try {
    return (await response.json()) as ProblemDetail
  } catch {
    return {}
  }
}
