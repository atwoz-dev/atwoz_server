package atwoz.atwoz.match.command.domain.match;

import java.util.Optional;

public interface MatchRepository {
    void save(Match match);

    boolean existsActiveMatchBetween(Long memberId, Long anotherMemberId);

    void withNamedLock(String key, Runnable action);

    Optional<Match> findById(Long id);
}
