package atwoz.atwoz.match.command.application.match;

import atwoz.atwoz.match.command.application.match.exception.ExistsMatchException;
import atwoz.atwoz.match.command.application.match.exception.MatchNotFoundException;
import atwoz.atwoz.match.command.domain.match.Match;
import atwoz.atwoz.match.command.domain.match.MatchRepository;
import atwoz.atwoz.match.command.domain.match.MatchStatus;
import atwoz.atwoz.match.command.domain.match.vo.Message;
import atwoz.atwoz.match.presentation.dto.MatchRequestDto;
import atwoz.atwoz.match.presentation.dto.MatchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;

    @Transactional
    public void request(Long requesterId, MatchRequestDto requestDto) {
        String key = generateKey(requesterId, requestDto.responderId());

        matchRepository.withNamedLock(key, () -> {
            if (existsMutualMatch(requesterId, requestDto.responderId())) {
                throw new ExistsMatchException();
            }

            Match match = Match.request(requesterId, requestDto.responderId(), Message.from(requestDto.requestMessage()));
            matchRepository.save(match);
        });
    }

    @Transactional
    public void approve(Long responderId, MatchResponseDto respondDto) {
        Match match = getWaitingMatchById(respondDto.matchId());
        match.approve();
    }

    @Transactional
    public void reject(Long responderId, MatchResponseDto respondDto) {
        Match match = getWaitingMatchById(respondDto.matchId());
        match.reject();
    }

    @Transactional
    public void rejectCheck(Long memberId, Long matchId) {
        Match match = getRejectedMatchById(matchId);
        match.checkRejected();
    }

    private boolean existsMutualMatch(Long requesterId, Long responderId) {
        return matchRepository.existsActiveMatchBetween(requesterId, responderId);
    }

    private String generateKey(Long requesterId, Long responderId) {
        return Math.max(requesterId, responderId) + ":" + Math.min(requesterId, responderId);
    }

    private Match getWaitingMatchById(Long id) {
        Match match = matchRepository.findById(id).orElseThrow(MatchNotFoundException::new);
        if (match.getStatus() != MatchStatus.WAITING) throw new MatchNotFoundException();
        return match;
    }

    private Match getRejectedMatchById(Long id) {
        Match match = matchRepository.findById(id).orElseThrow(MatchNotFoundException::new);
        if (match.getStatus() != MatchStatus.REJECTED) throw new MatchNotFoundException();
        return match;
    }
}
