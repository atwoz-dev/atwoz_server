package atwoz.atwoz.match.command.application.match;

import atwoz.atwoz.match.command.application.match.exception.ExistsMatchException;
import atwoz.atwoz.match.command.domain.match.Match;
import atwoz.atwoz.match.command.domain.match.MatchRepository;
import atwoz.atwoz.match.command.domain.match.vo.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;

    @Transactional
    public void request(Long requesterId, Long responderId, String requestMessage) {
        String key = generateKey(requesterId, responderId);

        matchRepository.withNamedLock(key, () -> {
            if (existsMutualMatch(requesterId, responderId)) {
                throw new ExistsMatchException();
            }

            Match match = Match.request(requesterId, responderId, Message.from(requestMessage));
            matchRepository.save(match);
        });
    }


    private boolean existsMutualMatch(Long requesterId, Long responderId) {
        return matchRepository.existsActiveMatchBetween(requesterId, responderId);
    }

    private String generateKey(Long requesterId, Long responderId) {
        return Math.max(requesterId, responderId) + "" + Math.min(requesterId, responderId);
    }
}
