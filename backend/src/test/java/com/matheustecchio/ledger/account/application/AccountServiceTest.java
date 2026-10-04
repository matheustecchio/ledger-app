package com.matheustecchio.ledger.account.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.matheustecchio.ledger.account.domain.Account;
import com.matheustecchio.ledger.account.domain.AccountRepository;
import com.matheustecchio.ledger.account.domain.AccountType;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccountServiceTest {

    private static final UUID USER_ID = UUID.fromString("3a78d0d8-c261-44d7-8c87-e2be10477cb0");

    @Test
    void scopesCreatedAndListedAccountsToTheCurrentUser() {
        var repository = new InMemoryAccountRepository();
        var clock = Clock.fixed(Instant.parse("2026-08-30T12:00:00Z"), ZoneOffset.UTC);
        var service = new AccountService(repository, () -> USER_ID, clock);

        var created = service.createAccount(new CreateAccountCommand(
                "Holiday savings",
                AccountType.SAVINGS,
                Currency.getInstance("EUR"),
                new BigDecimal("800.00")));

        assertThat(created.userId()).isEqualTo(USER_ID);
        assertThat(service.listAccounts()).containsExactly(created);
        assertThat(repository.lastRequestedUserId).isEqualTo(USER_ID);
    }

    private static final class InMemoryAccountRepository implements AccountRepository {
        private final List<Account> accounts = new ArrayList<>();
        private UUID lastRequestedUserId;

        @Override
        public Account save(Account account) {
            accounts.add(account);
            return account;
        }

        @Override
        public List<Account> findAllByUserId(UUID userId) {
            lastRequestedUserId = userId;
            return accounts.stream().filter(account -> account.userId().equals(userId)).toList();
        }
    }
}
