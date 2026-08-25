import { useCallback, useReducer, useRef } from 'react'
import type { CalculationRequest, CalculationResult } from '../types/calculator.types'
import { calculate as calculateViaApi, CalculatorApiError } from '../api/calculatorApi'

export type CalculatorState =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'success'; result: CalculationResult }
  | { status: 'error'; message: string }

type CalculatorAction =
  | { type: 'CALCULATE_STARTED' }
  | { type: 'CALCULATE_SUCCEEDED'; result: CalculationResult }
  | { type: 'CALCULATE_FAILED'; message: string }
  | { type: 'CLEARED' }

const initialState: CalculatorState = { status: 'idle' }

const UNEXPECTED_ERROR_MESSAGE = 'Something went wrong. Please try again.'

function calculatorReducer(_state: CalculatorState, action: CalculatorAction): CalculatorState {
  switch (action.type) {
    case 'CALCULATE_STARTED':
      return { status: 'loading' }
    case 'CALCULATE_SUCCEEDED':
      return { status: 'success', result: action.result }
    case 'CALCULATE_FAILED':
      return { status: 'error', message: action.message }
    case 'CLEARED':
      return initialState
  }
}

export function useCalculator() {
  const [state, dispatch] = useReducer(calculatorReducer, initialState)
  const latestRequestIdRef = useRef(0)

  const calculate = useCallback(async (request: CalculationRequest): Promise<void> => {
    const requestId = ++latestRequestIdRef.current
    dispatch({ type: 'CALCULATE_STARTED' })

    try {
      const result = await calculateViaApi(request)
      if (latestRequestIdRef.current === requestId) {
        dispatch({ type: 'CALCULATE_SUCCEEDED', result })
      }
    } catch (error) {
      if (latestRequestIdRef.current === requestId) {
        const message = error instanceof CalculatorApiError ? error.message : UNEXPECTED_ERROR_MESSAGE
        dispatch({ type: 'CALCULATE_FAILED', message })
      }
    }
  }, [])

  const clear = useCallback(() => {
    latestRequestIdRef.current++
    dispatch({ type: 'CLEARED' })
  }, [])

  return { state, calculate, clear }
}
