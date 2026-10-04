# Ledger

Ledger is a local-first, web-based personal finance management application built as a Java, Spring, GraphQL, and React portfolio project. It is a modular monolith: one backend deployment with domain-oriented internal boundaries and one browser client. The application and PostgreSQL database run on the user's machine; no cloud service is required for normal use.

The current architecture milestone proves one complete path:

> Create an account in React → send a GraphQL mutation → apply backend rules → persist it in PostgreSQL → query it through GraphQL → render it in React.

Ledger is early-stage development software. It is not a banking application and must not be used with real financial data yet. Authentication is deliberately scheduled for the next phase; the current backend assigns every account to a seeded development user.

## Stack

- Backend: Java 21, Spring Boot 4.1, Spring for GraphQL, Spring Security, Spring Data JPA, Flyway, PostgreSQL, Gradle
- Frontend: React 19, TypeScript, Vite, Apollo Client, React Router, React Hook Form, Zod
- Testing: JUnit 5, Spring GraphQL Tester, Testcontainers, Vitest, Testing Library, Playwright foundation
- Local infrastructure: Docker Compose

See [the architecture overview](docs/architecture/overview.md), [the account GraphQL API](docs/api/accounts.md), [ADR 0001](docs/decisions/0001-modular-monolith.md), and [ADR 0002](docs/decisions/0002-local-first-web-application.md) for the rationale and boundaries. Development commands and troubleshooting live in the [development guide](docs/development.md).

## Prerequisites

- Java 21
- Node.js 20.19 or newer
- npm 9 or newer
- Docker with Compose

Gradle does not need to be installed globally; the backend includes the Gradle wrapper.

## Run locally

The current development environment uses three local processes: PostgreSQL in Docker, Spring Boot through Gradle, and Vite through npm. A full-stack Compose profile or launcher that starts all three with one command is the next local-experience milestone; it is not implemented yet.

Copy the development configuration and start PostgreSQL:

```bash
cp .env.example .env
docker compose up -d
```

Start the backend:

```bash
cd backend
./gradlew bootRun
```

In a second terminal, start the frontend:

```bash
cd frontend
npm ci
npm run dev
```

Open `http://localhost:5173`. Vite proxies `/graphql` to the backend at `http://localhost:8080`. GraphiQL is available during development at `http://localhost:8080/graphiql`.

By default, the database and backend bind to the local loopback interface. Keep them local while the fixed development identity and open GraphQL endpoint remain in place.

Stop PostgreSQL without deleting its volume:

```bash
docker compose down
```

## Configuration

The checked-in [.env.example](.env.example) contains local-only defaults. Runtime values are read from environment variables; `.env` is ignored by Git.

| Variable | Purpose | Development default |
| --- | --- | --- |
| `LEDGER_DATABASE_URL` | Backend JDBC URL | `jdbc:postgresql://localhost:5432/ledger` |
| `LEDGER_DATABASE_USERNAME` | PostgreSQL user | `ledger` |
| `LEDGER_DATABASE_PASSWORD` | PostgreSQL password | `ledger_dev_only` |
| `LEDGER_SERVER_ADDRESS` | Backend bind address | `127.0.0.1` |
| `LEDGER_FRONTEND_ORIGIN` | Allowed browser origin | `http://localhost:5173` |
| `LEDGER_GRAPHIQL_ENABLED` | Enables the local GraphiQL UI | `true` |
| `VITE_GRAPHQL_URL` | Optional frontend GraphQL URL | `/graphql` |

The seeded development identity is fixed inside the backend configuration and migration. It is not an authentication mechanism.

## Verify changes

Backend:

```bash
cd backend
./gradlew test                 # fast unit tests
./gradlew integrationTest      # real PostgreSQL via Testcontainers
./gradlew jacocoTestReport     # HTML and XML coverage reports
./gradlew check                # aggregate backend verification
./gradlew build                # compile, verify, and package
```

Frontend:

```bash
cd frontend
npm run typecheck
npm test
npm run test:coverage
npm run build
```

The Playwright runner is configured, but the full-browser account journey remains skipped until the application-wide Compose profile is added. Do not report it as passing.

Coverage reports are generated at `backend/build/reports/jacoco/test/html/index.html` and `frontend/coverage/index.html`. No percentage threshold is enforced at this initial baseline.

## Repository layout

```text
backend/                 Spring Boot modular monolith
  src/main/java/.../
    account/             First domain module and vertical slice
    shared/              Cross-cutting time and security seams
  src/main/resources/
    graphql/             GraphQL schema
    db/migration/        Flyway migrations
frontend/                React application organized by feature
docs/architecture/       System boundaries and data flow
docs/api/                GraphQL contracts and examples
docs/decisions/          Architecture decision records
docker-compose.yml       Development PostgreSQL
```

## Scope

The next planned phase is users and cookie-based authentication, followed by a one-command local startup experience. Later phases add categories, transactions, transfers, dashboard calculations, budgets, recurring transactions, search, import/export, and UI polish. Cloud hosting, desktop packaging, banking integrations, payments, microservices, Kafka, and Kubernetes are intentionally out of scope for the MVP unless a later ADR changes the product direction.

## Contributing

Read [CONTRIBUTING.md](CONTRIBUTING.md) before making changes. It defines the required feature-branch, focused-commit, verification, and pull-request workflow. The longer [Git and GitHub workflow](docs/workflows/git-and-github.md) explains how local work moves through review without committing directly to `main`.

Repository automation is still being established. Follow the documented checks locally and do not infer that a passing GitHub status exists unless the repository contains and runs the corresponding workflow.

## Project documentation

- [Development and testing](docs/development.md)
- [Architecture overview](docs/architecture/overview.md)
- [Account GraphQL API](docs/api/accounts.md)
- [ADR 0001: Domain-oriented modular monolith](docs/decisions/0001-modular-monolith.md)
- [ADR 0002: Local-first web application](docs/decisions/0002-local-first-web-application.md)
- [Git and GitHub workflow](docs/workflows/git-and-github.md)
- [Repository guidance for coding agents](AGENT.md)

## License

Ledger is available under the [MIT License](LICENSE).
