package com.matheustecchio.ledger.account.domain;

import java.util.List;
import java.util.UUID;

public interface AccountRepository {

    Account save(Account account);

    List<Account> findAllByUserId(UUID userId);
}
