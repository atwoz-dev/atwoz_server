package atwoz.atwoz.match.command.infra.match;

import atwoz.atwoz.match.command.domain.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MatchJpaRepository extends JpaRepository<Match, Long> {

    @Query(value = """
            SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END
            FROM Match m
            WHERE (m.requesterId = :idOne AND m.responderId = :idTwo)
            OR (m.requesterId = :idTwo AND m.responderId = :idOne)
            AND m.status <> 'EXPIRED'
            """)
    boolean existsActiveMatchBetween(Long idOne, Long idTwo);


}
