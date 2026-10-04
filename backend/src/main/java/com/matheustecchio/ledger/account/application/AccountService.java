package com.matheustecchio.ledger.account.application;

import com.matheustecchio.ledger.account.domain.Account;
import com.matheustecchio.ledger.account.domain.AccountRepository;
import com.matheustecchio.ledger.shared.security.CurrentUserProvider;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CurrentUserProvider currentUserProvider;
    private final Clock clock;

    public AccountService(
            AccountRepository accountRepository,
            CurrentUserProvider currentUserProvider,
            Clock clock) {
        this.accountRepository = accountRepository;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    @Transactional
    public Account createAccount(CreateAccountCommand command) {
        var account = Account.create(
                currentUserProvider.userId(),
                command.name(),
                command.type(),
                command.currency(),
                command.initialBalance(),
                Instant.now(clock));
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public List<Account> listAccounts() {
        return accountRepository.findAllByUserId(currentUserProvider.userId());
    }
}
