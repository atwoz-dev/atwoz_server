package atwoz.atwoz.match.command.infra.match;

import atwoz.atwoz.common.repository.LockRepository;
import atwoz.atwoz.match.command.domain.match.Match;
import atwoz.atwoz.match.command.domain.match.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class MatchRepositoryImpl implements MatchRepository {
    private final MatchJpaRepository matchJpaRepository;
    private final LockRepository lockRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(Match match) {
        matchJpaRepository.save(match);
    }

    @Override
    public boolean existsActiveMatchBetween(Long idOne, Long idTwo) {
        return matchJpaRepository.existsActiveMatchBetween(idOne, idTwo);
    }

    @Override
    public void withNamedLock(String key, Runnable action) {
        try {
            lockRepository.getLock(key, 10);
            action.run();
        } finally {
            lockRepository.releaseLock(key);
        }
    }
}
