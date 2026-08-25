export const OPERATIONS = [
  'ADD',
  'SUBTRACT',
  'MULTIPLY',
  'DIVIDE',
  'POWER',
  'SQRT',
  'PERCENTAGE',
] as const

export type Operation = (typeof OPERATIONS)[number]

export type OperationArity = 'BINARY' | 'UNARY'

export const OPERATION_ARITY: Readonly<Record<Operation, OperationArity>> = {
  ADD: 'BINARY',
  SUBTRACT: 'BINARY',
  MULTIPLY: 'BINARY',
  DIVIDE: 'BINARY',
  POWER: 'BINARY',
  SQRT: 'UNARY',
  PERCENTAGE: 'BINARY',
}

export interface CalculationRequest {
  operation: Operation
  operandA: number
  operandB?: number
}

export interface CalculationResult {
  operation: Operation
  result: number
}

export interface ProblemDetail {
  type?: string
  title?: string
  status?: number
  detail?: string
  instance?: string
  errors?: string[]
}
