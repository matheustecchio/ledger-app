# Development and testing

## Prerequisites

- Java 21
- Node.js 20.19 or newer
- npm 9 or newer
- Docker with Compose

The backend uses its checked-in Gradle wrapper. The frontend lockfile is authoritative; use `npm ci` for a reproducible install.

## Start the development environment

The application is web-based but all services and data run locally. The current Phase 0 workflow requires PostgreSQL, Spring Boot, and Vite to be started separately. A one-command full-stack Compose profile or launcher is planned but is not available yet.

From the repository root:

```bash
cp .env.example .env
docker compose up -d
```

The Compose file starts PostgreSQL 17.6, binds it to `127.0.0.1`, and persists local data in the `ledger-postgres-data` volume.

Start the backend in one terminal:

```bash
cd backend
./gradlew bootRun
```

Start the frontend in another terminal:

```bash
cd frontend
npm ci
npm run dev
```

Open `http://localhost:5173`. Vite proxies `/graphql` to `http://localhost:8080`. The local GraphiQL UI is at `http://localhost:8080/graphiql`.

Spring Boot binds to `127.0.0.1` by default. Do not change the database or backend bind address to a network-accessible interface while the development identity and open GraphQL endpoint are in use.

## Configuration

Local defaults are documented in `.env.example`. Keep personal overrides in `.env`, which is ignored by Git. Never commit production secrets or real credentials.

The backend reads its JDBC URL, username, password, bind address, allowed frontend origin, and GraphiQL setting from environment variables. `VITE_GRAPHQL_URL` optionally overrides the frontend endpoint; its default is `/graphql`.

## Backend verification

Run commands from `backend/`:

```bash
./gradlew test
./gradlew integrationTest
./gradlew jacocoTestReport
./gradlew check
./gradlew build
```

- `test` runs fast unit tests and excludes the `integration` JUnit tag.
- `integrationTest` starts PostgreSQL 17.6 with Testcontainers and verifies Flyway, JPA, Spring Security, and GraphQL over HTTP.
- `jacocoTestReport` combines unit and integration execution data into XML and HTML reports.
- `check` runs both unit and integration suites.
- `build` compiles, verifies, and packages the Spring Boot application.

The HTML coverage report is written to `backend/build/reports/jacoco/test/html/index.html`.

## Frontend verification

Run commands from `frontend/`:

```bash
npm run typecheck
npm test
npm run test:coverage
npm run build
```

- `typecheck` checks application, Vite, and Playwright TypeScript configuration without emitting files.
- `test` runs Vitest tests under `src/`.
- `test:coverage` writes the HTML report to `frontend/coverage/index.html`.
- `build` type-checks and creates the production bundle under `frontend/dist/`.

`npm run test:e2e` is configured for Playwright, but the current account journey is explicitly skipped until the full application Compose profile exists. Do not count it as a passing test.

## Match checks to changes

- Run backend unit and integration tests for Java behavior or configuration changes.
- Run the integration suite for Flyway or JPA changes.
- Run backend and frontend checks for GraphQL schema changes.
- Run frontend typecheck, tests, and build for UI or client changes.
- For documentation-only changes, validate links and commands against manifests and run `git diff --check`; a full application build is optional unless a claim is disputed.

## Stop the environment

Stop containers while preserving the PostgreSQL volume:

```bash
docker compose down
```

Deleting the volume also deletes local database data and should only be done intentionally.

## Troubleshooting

### PostgreSQL does not become healthy

Run `docker compose ps` and inspect the PostgreSQL service logs. Confirm that port `5432`, or the configured `LEDGER_DATABASE_PORT`, is not already in use.

### The backend cannot connect to PostgreSQL

Confirm the container is healthy and that `LEDGER_DATABASE_URL`, `LEDGER_DATABASE_USERNAME`, and `LEDGER_DATABASE_PASSWORD` match the Compose values. Do not print secret-bearing production environment values while diagnosing the problem.

### npm reports an unsupported engine

Check `node --version`. The frontend requires Node.js 20.19 or newer because its pinned Vite, React Router, and Playwright toolchain does not support Node 18.

### Flyway or Hibernate validation fails

Treat this as a schema/mapping mismatch. Add or correct a Flyway migration and align the JPA mapping; do not enable Hibernate schema mutation to bypass validation.
