import { describe, expect, it } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import App from './App'

describe('Calculator', () => {
  it('calculates a binary operation and shows the result', async () => {
    const user = userEvent.setup()
    render(<App />)

    await user.type(screen.getByLabelText('a'), '2')
    await user.type(screen.getByLabelText('b'), '3')
    await user.click(screen.getByRole('button', { name: 'Calculate' }))

    expect(await screen.findByText('5')).toBeInTheDocument()
  })

  it('hides operand b for the unary square root operation', async () => {
    const user = userEvent.setup()
    render(<App />)

    await user.selectOptions(screen.getByLabelText('Operation'), 'SQRT')
    expect(screen.queryByLabelText('b')).not.toBeInTheDocument()

    await user.type(screen.getByLabelText('a'), '16')
    await user.click(screen.getByRole('button', { name: 'Calculate' }))

    expect(await screen.findByText('4')).toBeInTheDocument()
  })

  it('validates non-numeric input locally', async () => {
    const user = userEvent.setup()
    render(<App />)

    await user.type(screen.getByLabelText('a'), 'abc')
    await user.type(screen.getByLabelText('b'), '3')
    await user.click(screen.getByRole('button', { name: 'Calculate' }))

    expect(await screen.findByText('Enter a valid number')).toBeInTheDocument()
    expect(screen.getByRole('status')).toBeEmptyDOMElement()
  })

  it('submits the form with the Enter key', async () => {
    const user = userEvent.setup()
    render(<App />)

    await user.type(screen.getByLabelText('a'), '7')
    await user.type(screen.getByLabelText('b'), '6')
    await user.keyboard('{Enter}')

    expect(await screen.findByText('13')).toBeInTheDocument()
  })

})
