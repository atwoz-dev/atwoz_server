package atwoz.atwoz.match.command.domain.match;

public interface MatchRepository {
    void save(Match match);

    boolean existsActiveMatchBetween(Long memberId, Long anotherMemberId);

    void withNamedLock(String key, Runnable action);
}
