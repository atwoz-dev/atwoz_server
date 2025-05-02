package atwoz.atwoz.match.command.infra.match;

import atwoz.atwoz.match.command.domain.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MatchJpaRepository extends JpaRepository<Match, Long> {

    @Query(value = """
        SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false 
        END
        FROM Match m
        WHERE ((m.requesterId = :memberId AND m.responderId = :anotherMemberId)
        OR (m.requesterId = :anotherMemberId AND m.responderId = :memberId))
        AND m.status <> 'EXPIRED' AND m.status <> 'REJECT_CHECKED'
        """)
    boolean existsActiveMatchBetween(Long memberId, Long anotherMemberId);

    Optional<Match> findByIdAndRequesterId(Long id, Long requesterId);

    Optional<Match> findByIdAndResponderId(Long id, Long responderId);
}
