package com.matheustecchio.ledger.account.graphql;

import com.matheustecchio.ledger.account.application.AccountService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AccountGraphqlController {

    private final AccountService accountService;

    public AccountGraphqlController(AccountService accountService) {
        this.accountService = accountService;
    }

    @QueryMapping
    public List<AccountPayload> accounts() {
        return accountService.listAccounts().stream()
                .map(AccountPayload::fromDomain)
                .toList();
    }

    @MutationMapping
    public AccountPayload createAccount(@Valid @Argument CreateAccountInput input) {
        return AccountPayload.fromDomain(accountService.createAccount(input.toCommand()));
    }
}
