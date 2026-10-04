# Architecture overview

## System shape

Ledger is a local-first web application in a monorepo. It contains a React browser client and one Spring Boot backend, with PostgreSQL as the system of record. The frontend, backend, and database run on the user's machine and do not require cloud services for normal use. The backend is a modular monolith: domain modules are isolated in code while sharing one process and database.

```text
Browser on the local machine
        |
        v
React + TypeScript (localhost)
        |
        | GraphQL over HTTP
        v
Spring GraphQL resolver (localhost)
        |
        v
Application service
        |
        v
Domain model and repository interface
        |
        v
JPA repository adapter
        |
        v
PostgreSQL (local persistent volume; schema owned by Flyway)
```

The browser never connects directly to PostgreSQL. It sends GraphQL requests to Spring Boot, which owns validation, authorization boundaries, financial behavior, and persistence. The development server and database bind to the loopback interface by default.

## Product delivery decision

Ledger remains browser-based rather than being packaged with Electron or Tauri. This preserves the Java/Spring/GraphQL architecture, keeps one frontend implementation, and leaves open a future hosted deployment without making remote hosting a requirement.

The intended local experience is one command or launcher that starts PostgreSQL, Spring Boot, and the built frontend, followed by opening a local URL. The current Phase 0 setup still starts PostgreSQL, the backend, and the Vite development server separately. Full-stack Compose or an equivalent launcher is deferred to a later implementation milestone. See [ADR 0002](../decisions/0002-local-first-web-application.md).

The first implemented path is the `account` module. Future modules should follow the same dependency direction without introducing repository-wide `controller`, `service`, `entity`, or `repository` packages.

## Backend module boundaries

Each domain module may contain:

- `domain`: business types and repository contracts; no Spring or frontend dependencies
- `application`: use cases, transactions, and orchestration
- `infrastructure`: JPA entities and adapters implementing domain contracts
- `graphql`: schema-facing inputs, payloads, and thin resolvers

Shared code is limited to genuine cross-cutting concerns. `shared/security/CurrentUserProvider` is the identity seam used by application services. The current implementation returns a configured development user; Phase 1 must replace it with an authenticated session implementation.

## Account data model

An account owns these persisted values:

- UUID identifier and owning user UUID
- normalized name
- account type: current, savings, cash, or credit card
- ISO 4217 currency code
- initial balance as `NUMERIC(19, 4)` / `BigDecimal`
- archived flag
- UTC creation and update timestamps

The GraphQL API represents the precise decimal as a string. This prevents a browser or JSON parser from introducing binary floating-point rounding before the backend constructs `BigDecimal`.

## Ownership and security milestone

Resource ownership is always decided server-side. The account mutation intentionally has no `userId` argument, and account queries filter by the current user ID.

This milestone is not production-secure: `/graphql` is open and a seeded development identity owns all resources. Spring Security, CORS, and CSRF boundaries are installed so Phase 1 can add session authentication without moving authorization decisions into the frontend. Production deployment is blocked until that replacement is complete.

## Persistence

- Flyway is the only schema-change mechanism.
- Hibernate uses `ddl-auto: validate`; it never creates or updates the schema.
- PostgreSQL-backed integration tests run against the same migration and mapping as development.
- System timestamps use `Instant` and the injected application clock is UTC.

## Frontend boundaries

The frontend is organized by feature. Account-specific UI, GraphQL documents, and types live under `src/features/accounts`. Application providers and routing live under `src/app`; reusable cross-feature components can be introduced only when a real second consumer exists.

Apollo Client owns GraphQL server state. React Hook Form and Zod own form state and client-side feedback. The backend remains authoritative for financial validation and ownership.

## Intentionally deferred

- Cookie-based login and Argon2id password hashing
- update/archive account operations
- calculated balances from transactions
- categories, transactions, transfers, budgets, recurring transactions, and reporting
- CSV import/export
- one-command full-stack Compose or launcher and enabled Playwright journeys
- desktop packaging, cloud hosting, CI/CD, and deployment
