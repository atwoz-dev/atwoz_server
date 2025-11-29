package deepple.deepple.match.command.infra.match;

import deepple.deepple.match.command.domain.match.Match;
import deepple.deepple.match.command.domain.match.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MatchRepositoryImpl implements MatchRepository {
    private final MatchJpaRepository matchJpaRepository;

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
    public Optional<Match> findById(Long id) {
        return matchJpaRepository.findById(id);
    }

    @Override
    public Optional<Match> findByIdAndRequesterId(Long id, Long requesterId) {
        return matchJpaRepository.findByIdAndRequesterId(id, requesterId);
    }

    @Override
    public Optional<Match> findByIdAndResponderId(Long id, Long responderId) {
        return matchJpaRepository.findByIdAndResponderId(id, responderId);
    }

    @Override
    public Optional<Match> findByRequesterIdAndResponderId(Long requesterId, Long responderId) {
        return matchJpaRepository.findByRequesterIdAndResponderId(requesterId, responderId);
    }
}
