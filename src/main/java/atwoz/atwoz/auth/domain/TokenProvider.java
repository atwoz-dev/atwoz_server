package atwoz.atwoz.auth.domain;

import atwoz.atwoz.common.Role;

import java.time.Instant;

public interface TokenProvider {

    String createAccessToken(long id, Role role, Instant issuedAt);

    String createRefreshToken(long id, Role role, Instant issuedAt);
}
