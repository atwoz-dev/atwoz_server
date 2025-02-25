package atwoz.atwoz.match.command.infra.match;

import atwoz.atwoz.common.exception.CannotGetLockException;
import atwoz.atwoz.common.repository.LockRepository;
import atwoz.atwoz.match.command.domain.match.Match;
import atwoz.atwoz.match.command.domain.match.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public boolean existsActiveMatchBetween(Long memberId, Long anotherMemberId) {
        return matchJpaRepository.existsActiveMatchBetween(memberId, anotherMemberId);
    }

    @Override
    public void withNamedLock(String key, Runnable action) {
        try {
            lockRepository.getLock(key, 10);
            action.run();
        } catch (DataAccessException e) {
            throw new CannotGetLockException();
        } finally {
            lockRepository.releaseLock(key);
        }
    }

    @Override
    public Optional<Match> findById(Long id) {
        return matchJpaRepository.findById(id);
    }
}
