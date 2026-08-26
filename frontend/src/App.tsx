import './styles.css'
import { CalculatorDisplay } from './features/calculator/components/CalculatorDisplay'
import { CalculatorForm } from './features/calculator/components/CalculatorForm'
import { useCalculator } from './features/calculator/hooks/useCalculator'

function App() {
  const { state, calculate, clear } = useCalculator()

  return (
    <main className="app">
      <h1>Calculator</h1>
      <section className="calculator" aria-label="Calculator">
        <CalculatorDisplay state={state} />
        <CalculatorForm onCalculate={calculate} onClear={clear} loading={state.status === 'loading'} />
      </section>
    </main>
  )
}

export default App
