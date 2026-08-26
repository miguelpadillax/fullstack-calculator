import type { ChangeEvent } from 'react'

interface OperandInputProps {
  id: string
  label: string
  value: string
  error?: string
  onChange: (value: string) => void
}

export function OperandInput({ id, label, value, error, onChange }: OperandInputProps) {
  return (
    <div className="field">
      <label htmlFor={id}>{label}</label>
      <input
        id={id}
        type="text"
        inputMode="decimal"
        autoComplete="off"
        value={value}
        onChange={(event: ChangeEvent<HTMLInputElement>) => onChange(event.target.value)}
        aria-invalid={error ? true : undefined}
        aria-describedby={error ? `${id}-error` : undefined}
        className={error ? 'invalid' : undefined}
      />
      {error && (
        <p className="field-error" id={`${id}-error`}>
          {error}
        </p>
      )}
    </div>
  )
}
