package com.matheustecchio.ledger.account.infrastructure;

import com.matheustecchio.ledger.account.domain.Account;
import com.matheustecchio.ledger.account.domain.AccountRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
class JpaAccountRepository implements AccountRepository {

    private final SpringDataAccountRepository repository;

    JpaAccountRepository(SpringDataAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Account save(Account account) {
        return repository.save(AccountJpaEntity.fromDomain(account)).toDomain();
    }

    @Override
    public List<Account> findAllByUserId(UUID userId) {
        return repository.findAllByUserIdOrderByCreatedAtAsc(userId).stream()
                .map(AccountJpaEntity::toDomain)
                .toList();
    }
}
