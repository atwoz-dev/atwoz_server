package atwoz.atwoz.auth.domain;

public interface TokenRepository {

    void save(String token);

    void delete(String token);

    boolean exists(String token);
}
