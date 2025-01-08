package atwoz.atwoz.auth.domain;

import java.time.Duration;

public interface TokenRepository {

    void save(String token, Duration expiration);

    void delete(String token);

    boolean exists(String token);
}
