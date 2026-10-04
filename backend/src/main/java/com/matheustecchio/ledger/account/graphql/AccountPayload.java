package com.matheustecchio.ledger.account.graphql;

import com.matheustecchio.ledger.account.domain.Account;
import com.matheustecchio.ledger.account.domain.AccountType;
import java.util.UUID;

public record AccountPayload(
        UUID id,
        String name,
        AccountType type,
        String currency,
        String initialBalance,
        boolean archived,
        String createdAt,
        String updatedAt) {

    static AccountPayload fromDomain(Account account) {
        return new AccountPayload(
                account.id(),
                account.name(),
                account.type(),
                account.currency().getCurrencyCode(),
                account.initialBalance().toPlainString(),
                account.archived(),
                account.createdAt().toString(),
                account.updatedAt().toString());
    }
}
