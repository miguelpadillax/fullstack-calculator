# Calculator Frontend

SPA made with React 19 + TypeScript, Vite 6 and Vitest 4.

## Run

```bash
npm install
npm run dev
```

Requires Node 20+. The dev server proxies `/api` to `http://localhost:8080`.  
Start the backend first (see `../backend/README.md`).

## Test & coverage

```bash
npm test
npm run test:coverage
```

Coverage is enforced at 80% lines/branches/functions/statements — the build fails below
it, mirroring the backend JaCoCo rule. Report: `coverage/index.html`.

## Configuration

| Variable | Default | Description |
|---|---|---|
| `VITE_API_BASE_URL` | _(empty)_ | Base URL of the API. Empty = relative paths via the dev proxy. Set it when the backend is deployed elsewhere (see `.env.example`). |

## UI

- Form-based `a op b` entry; operand `b` is hidden for unary operations (`SQRT`)
- Local syntax validation (numeric, required) so no request leaves the browser on invalid
  input; mathematical errors (division by zero, sqrt of negative) come back from the
  backend as problem details
- Keyboard: `Enter` = calculate, `Escape` = clear inputs and result, `Tab` navigates
- Light/dark theme follows the OS setting; layout works down to 360px viewports

## Structure

```
src/
├── features/calculator/      # feature module, not file-type folders
│   ├── api/                  # typed API client + error mapping (ProblemDetail → CalculatorApiError)
│   ├── components/           # form, display, inputs, operation selector
│   ├── hooks/                # useCalculator: state machine (idle/loading/success/error)
│   └── types/                # mirror of the backend contract
├── test/
└── App.tsx
```
