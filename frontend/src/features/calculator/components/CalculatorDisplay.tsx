import type { CalculatorState } from '../hooks/useCalculator'

export function CalculatorDisplay({ state }: { state: CalculatorState }) {
  return (
    <div className="display" role="status">
      {state.status === 'loading' && <p className="display-message">Calculating…</p>}
      {state.status === 'success' && (
        <p className="display-result">{String(state.result.result)}</p>
      )}
      {state.status === 'error' && <p className="display-error">{state.message}</p>}
    </div>
  )
}
