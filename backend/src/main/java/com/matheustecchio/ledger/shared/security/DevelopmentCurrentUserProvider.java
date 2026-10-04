package com.matheustecchio.ledger.shared.security;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
final class DevelopmentCurrentUserProvider implements CurrentUserProvider {

    private final UUID developmentUserId;

    DevelopmentCurrentUserProvider(
            @Value("${ledger.security.development-user-id}") UUID developmentUserId) {
        this.developmentUserId = developmentUserId;
    }

    @Override
    public UUID userId() {
        return developmentUserId;
    }
}
