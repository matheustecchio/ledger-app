package com.matheustecchio.ledger.account.graphql;

import com.matheustecchio.ledger.account.application.CreateAccountCommand;
import com.matheustecchio.ledger.account.domain.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Currency;

public record CreateAccountInput(
        @NotBlank @Size(max = 100) String name,
        @NotNull AccountType type,
        @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency,
        @NotBlank String initialBalance) {

    CreateAccountCommand toCommand() {
        return new CreateAccountCommand(
                name,
                type,
                Currency.getInstance(currency),
                new BigDecimal(initialBalance));
    }
}
