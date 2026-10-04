package com.matheustecchio.ledger.account.application;

import com.matheustecchio.ledger.account.domain.AccountType;
import java.math.BigDecimal;
import java.util.Currency;

public record CreateAccountCommand(
        String name,
        AccountType type,
        Currency currency,
        BigDecimal initialBalance) {}
