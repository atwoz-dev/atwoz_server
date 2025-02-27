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
        Match match = getWaitingMatchByIdAndResponderId(respondDto.matchId(), responderId);
        match.approve(Message.from(respondDto.responseMessage()));
    }

    @Transactional
    public void reject(Long responderId, MatchResponseDto respondDto) {
        Match match = getWaitingMatchByIdAndResponderId(respondDto.matchId(), responderId);
        match.reject(Message.from(respondDto.responseMessage()));
    }

    @Transactional
    public void rejectCheck(Long memberId, Long matchId) {
        Match match = getRejectedMatchByIdAndRequesterId(matchId, memberId);
        match.checkRejected();
    }

    private boolean existsMutualMatch(Long requesterId, Long responderId) {
        return matchRepository.existsActiveMatchBetween(requesterId, responderId);
    }

    private String generateKey(Long requesterId, Long responderId) {
        return Math.max(requesterId, responderId) + ":" + Math.min(requesterId, responderId);
    }

    private Match getWaitingMatchByIdAndResponderId(Long id, Long responderId) {
        Match match = matchRepository.findByIdAndResponderId(id, responderId)
                .orElseThrow(MatchNotFoundException::new);

        if (match.getStatus() != MatchStatus.WAITING) throw new MatchNotFoundException();
        return match;
    }

    private Match getRejectedMatchByIdAndRequesterId(Long id, Long requesterId) {
        Match match = matchRepository.findByIdAndRequesterId(id, requesterId)
                .orElseThrow(MatchNotFoundException::new);

        if (match.getStatus() != MatchStatus.REJECTED) throw new MatchNotFoundException();
        return match;
    }
}
