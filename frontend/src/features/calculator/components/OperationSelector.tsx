import { OPERATIONS } from '../types/calculator.types'
import type { Operation } from '../types/calculator.types'

const OPERATION_LABELS: Record<Operation, string> = {
  ADD: 'Add (+)',
  SUBTRACT: 'Subtract (−)',
  MULTIPLY: 'Multiply (×)',
  DIVIDE: 'Divide (÷)',
  POWER: 'Power (xʸ)',
  SQRT: 'Square root (√)',
  PERCENTAGE: 'Percentage (% of)',
}

interface OperationSelectorProps {
  id: string
  value: Operation
  onChange: (operation: Operation) => void
}

export function OperationSelector({ id, value, onChange }: OperationSelectorProps) {
  return (
    <div className="field">
      <label htmlFor={id}>Operation</label>
      <select
        id={id}
        value={value}
        onChange={(event) => onChange(event.target.value as Operation)}
      >
        {OPERATIONS.map((operation) => (
          <option key={operation} value={operation}>
            {OPERATION_LABELS[operation]}
          </option>
        ))}
      </select>
    </div>
  )
}
