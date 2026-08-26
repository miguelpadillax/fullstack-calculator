import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { OPERATION_ARITY } from '../types/calculator.types'
import type { CalculationRequest, Operation } from '../types/calculator.types'
import { OperandInput } from './OperandInput'
import { OperationSelector } from './OperationSelector'

const NUMBER_PATTERN = /^-?\d+(\.\d+)?$/

function isNumeric(value: string): boolean {
  return NUMBER_PATTERN.test(value.trim())
}

interface OperandErrors {
  operandA?: string
  operandB?: string
}

function validate(operandA: string, operandB: string, operation: Operation): OperandErrors | null {
  const errors: OperandErrors = {}

  if (!operandA.trim()) {
    errors.operandA = 'Enter a number'
  } else if (!isNumeric(operandA)) {
    errors.operandA = 'Enter a valid number'
  }

  if (OPERATION_ARITY[operation] === 'BINARY') {
    if (!operandB.trim()) {
      errors.operandB = 'Enter a number'
    } else if (!isNumeric(operandB)) {
      errors.operandB = 'Enter a valid number'
    }
  }

  return Object.keys(errors).length > 0 ? errors : null
}

interface CalculatorFormProps {
  onCalculate: (request: CalculationRequest) => void
  onClear: () => void
  loading: boolean
}

export function CalculatorForm({ onCalculate, onClear, loading }: CalculatorFormProps) {
  const [operandA, setOperandA] = useState('')
  const [operandB, setOperandB] = useState('')
  const [operation, setOperation] = useState<Operation>('ADD')
  const [submitted, setSubmitted] = useState(false)

  const isUnary = OPERATION_ARITY[operation] === 'UNARY'
  const errors = validate(operandA, operandB, operation)

  const reset = useCallback(() => {
    setOperandA('')
    setOperandB('')
    setOperation('ADD')
    setSubmitted(false)
  }, [])

  useEffect(() => {
    function handleKeyDown(event: KeyboardEvent) {
      if (event.key === 'Escape') {
        reset()
        onClear()
      }
    }
    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [reset, onClear])

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSubmitted(true)
    if (errors) {
      return
    }
    onCalculate({
      operation,
      operandA: Number(operandA.trim()),
      ...(isUnary ? {} : { operandB: Number(operandB.trim()) }),
    })
  }

  return (
    <form className="calculator-form" onSubmit={handleSubmit} noValidate>
      <div className={`operands${isUnary ? ' unary' : ''}`}>
        <OperandInput
          id="operand-a"
          label="a"
          value={operandA}
          error={submitted ? errors?.operandA : undefined}
          onChange={setOperandA}
        />
        <OperationSelector id="operation" value={operation} onChange={setOperation} />
        {!isUnary && (
          <OperandInput
            id="operand-b"
            label="b"
            value={operandB}
            error={submitted ? errors?.operandB : undefined}
            onChange={setOperandB}
          />
        )}
      </div>
      <button type="submit" className="calculate-button" disabled={loading}>
        {loading ? 'Calculating…' : 'Calculate'}
      </button>
    </form>
  )
}
