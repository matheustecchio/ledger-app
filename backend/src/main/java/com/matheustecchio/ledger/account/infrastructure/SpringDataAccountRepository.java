package com.matheustecchio.ledger.account.infrastructure;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataAccountRepository extends JpaRepository<AccountJpaEntity, UUID> {

    List<AccountJpaEntity> findAllByUserIdOrderByCreatedAtAsc(UUID userId);
}
