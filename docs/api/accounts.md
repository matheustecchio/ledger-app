# Account GraphQL API

The development endpoint is `POST http://localhost:8080/graphql`. GraphiQL is enabled locally at `http://localhost:8080/graphiql`.

## Create an account

```graphql
mutation CreateAccount {
  createAccount(
    input: {
      name: "Everyday account"
      type: CURRENT
      currency: "EUR"
      initialBalance: "425.50"
    }
  ) {
    id
    name
    type
    currency
    initialBalance
    archived
    createdAt
    updatedAt
  }
}
```

The initial balance is a decimal string with at most four fractional digits and must fit PostgreSQL `NUMERIC(19, 4)`. Currency is an uppercase ISO 4217 code. The server assigns the owning user and ignores no client-supplied ownership value because none exists in the input.

## List accounts

```graphql
query Accounts {
  accounts {
    id
    name
    type
    currency
    initialBalance
    archived
  }
}
```

The result is ordered by creation time and scoped to the current server-side user identity.

## Account types

```text
CURRENT
SAVINGS
CASH
CREDIT_CARD
```

## Current security limitation

The account contract is ready for user-scoped authorization, but authentication is not implemented in this milestone. All local requests use a seeded development user. Do not expose this API publicly or use real financial data.
