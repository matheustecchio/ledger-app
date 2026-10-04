package com.matheustecchio.ledger.shared.security;

import java.util.UUID;

public interface CurrentUserProvider {

    UUID userId();
}
