import { renderHook, act } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { useCalculator } from './useCalculator'
import * as calculatorApi from '../api/calculatorApi'

vi.mock('../api/calculatorApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../api/calculatorApi')>()),
}))

describe('useCalculator', () => {
  it('starts idle', () => {
    const { result } = renderHook(() => useCalculator())

    expect(result.current.state).toEqual({ status: 'idle' })
  })

  it('transitions from loading to success and exposes the result', async () => {
    const { result } = renderHook(() => useCalculator())

    let pending!: Promise<void>
    act(() => {
      pending = result.current.calculate({ operation: 'ADD', operandA: 2, operandB: 3 })
    })
    expect(result.current.state).toEqual({ status: 'loading' })

    await act(async () => {
      await pending
    })
    expect(result.current.state).toEqual({
      status: 'success',
      result: { operation: 'ADD', result: 5 },
    })
  })

  it('exposes the backend problem detail on domain errors', async () => {
    const { result } = renderHook(() => useCalculator())

    await act(async () => {
      await result.current.calculate({ operation: 'DIVIDE', operandA: 10, operandB: 0 })
    })

    expect(result.current.state).toEqual({ status: 'error', message: 'Cannot divide 10 by 0' })
  })

  it('falls back to a generic message for non-API errors', async () => {
    vi.spyOn(calculatorApi, 'calculate').mockRejectedValueOnce(new Error('boom'))

    const { result } = renderHook(() => useCalculator())

    await act(async () => {
      await result.current.calculate({ operation: 'ADD', operandA: 1, operandB: 1 })
    })

    expect(result.current.state).toEqual({
      status: 'error',
      message: 'Something went wrong. Please try again.',
    })
  })

  it('resets to idle on clear', async () => {
    const { result } = renderHook(() => useCalculator())

    await act(async () => {
      await result.current.calculate({ operation: 'ADD', operandA: 2, operandB: 3 })
    })
    act(() => {
      result.current.clear()
    })

    expect(result.current.state).toEqual({ status: 'idle' })
  })

})
