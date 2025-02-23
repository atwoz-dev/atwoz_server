package atwoz.atwoz.match.command.domain.match;

public interface MatchRepository {
    void save(Match match);

    boolean existsActiveMatchBetween(Long idOne, Long idTwo);

    void withNamedLock(String key, Runnable action);
}
