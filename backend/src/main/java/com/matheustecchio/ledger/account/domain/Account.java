package com.matheustecchio.ledger.account.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

public record Account(
        UUID id,
        UUID userId,
        String name,
        AccountType type,
        Currency currency,
        BigDecimal initialBalance,
        boolean archived,
        Instant createdAt,
        Instant updatedAt) {

    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_MONEY_PRECISION = 19;
    private static final int MAX_MONEY_SCALE = 4;

    public Account {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(userId, "userId is required");
        name = requireValidName(name);
        Objects.requireNonNull(type, "type is required");
        Objects.requireNonNull(currency, "currency is required");
        initialBalance = requireValidMoney(initialBalance);
        Objects.requireNonNull(createdAt, "createdAt is required");
        Objects.requireNonNull(updatedAt, "updatedAt is required");
    }

    public static Account create(
            UUID userId,
            String name,
            AccountType type,
            Currency currency,
            BigDecimal initialBalance,
            Instant now) {
        return new Account(
                UUID.randomUUID(),
                userId,
                name,
                type,
                currency,
                initialBalance,
                false,
                now,
                now);
    }

    private static String requireValidName(String value) {
        Objects.requireNonNull(value, "name is required");
        var normalized = value.trim();
        if (normalized.isEmpty() || normalized.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("name must contain between 1 and 100 characters");
        }
        return normalized;
    }

    private static BigDecimal requireValidMoney(BigDecimal value) {
        Objects.requireNonNull(value, "initialBalance is required");
        if (value.scale() > MAX_MONEY_SCALE) {
            throw new IllegalArgumentException("initialBalance must fit NUMERIC(19, 4)");
        }
        var normalized = value.setScale(MAX_MONEY_SCALE);
        if (normalized.precision() > MAX_MONEY_PRECISION) {
            throw new IllegalArgumentException("initialBalance must fit NUMERIC(19, 4)");
        }
        return normalized;
    }
}
