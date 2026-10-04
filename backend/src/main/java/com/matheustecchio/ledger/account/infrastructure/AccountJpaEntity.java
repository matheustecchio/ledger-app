package com.matheustecchio.ledger.account.infrastructure;

import com.matheustecchio.ledger.account.domain.Account;
import com.matheustecchio.ledger.account.domain.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

@Entity
@Table(name = "account")
class AccountJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AccountType type;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "initial_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal initialBalance;

    @Column(nullable = false)
    private boolean archived;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AccountJpaEntity() {}

    private AccountJpaEntity(Account account) {
        this.id = account.id();
        this.userId = account.userId();
        this.name = account.name();
        this.type = account.type();
        this.currency = account.currency().getCurrencyCode();
        this.initialBalance = account.initialBalance();
        this.archived = account.archived();
        this.createdAt = account.createdAt();
        this.updatedAt = account.updatedAt();
    }

    static AccountJpaEntity fromDomain(Account account) {
        return new AccountJpaEntity(account);
    }

    Account toDomain() {
        return new Account(
                id,
                userId,
                name,
                type,
                Currency.getInstance(currency),
                initialBalance,
                archived,
                createdAt,
                updatedAt);
    }
}
