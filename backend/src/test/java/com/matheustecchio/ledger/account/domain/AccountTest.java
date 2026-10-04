package com.matheustecchio.ledger.account.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccountTest {

    private static final UUID USER_ID = UUID.fromString("7cba9aa0-829c-48cb-b163-f3b97b64c345");
    private static final Instant NOW = Instant.parse("2026-08-30T10:15:30Z");

    @Test
    void createsAnAccountWithNormalizedFinancialValues() {
        var account = Account.create(
                USER_ID,
                "  Everyday account  ",
                AccountType.CURRENT,
                Currency.getInstance("EUR"),
                new BigDecimal("125.5"),
                NOW);

        assertThat(account.userId()).isEqualTo(USER_ID);
        assertThat(account.name()).isEqualTo("Everyday account");
        assertThat(account.initialBalance()).isEqualByComparingTo("125.5000");
        assertThat(account.archived()).isFalse();
        assertThat(account.createdAt()).isEqualTo(NOW);
    }

    @Test
    void rejectsMoneyThatCannotFitTheDatabaseColumn() {
        assertThatThrownBy(() -> Account.create(
                        USER_ID,
                        "Cash",
                        AccountType.CASH,
                        Currency.getInstance("EUR"),
                        new BigDecimal("1.12345"),
                        NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("NUMERIC(19, 4)");

        assertThatThrownBy(() -> Account.create(
                        USER_ID,
                        "Cash",
                        AccountType.CASH,
                        Currency.getInstance("EUR"),
                        new BigDecimal("1234567890123456.0000"),
                        NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("NUMERIC(19, 4)");
    }
}
