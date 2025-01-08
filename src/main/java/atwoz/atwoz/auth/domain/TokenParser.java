package atwoz.atwoz.auth.domain;

import java.time.Instant;

public interface TokenParser {

    boolean isValid(String token);

    boolean isExpired(String token);

    long getId(String token);

    Role getRole(String token);

    Instant getExpiration(String token);
}
