package atwoz.atwoz.match.command.application.match;

import atwoz.atwoz.common.repository.LockRepository;
import atwoz.atwoz.match.command.application.match.exception.ExistsMatchException;
import atwoz.atwoz.match.command.application.match.exception.InvalidMatchUpdateException;
import atwoz.atwoz.match.command.application.match.exception.MatchNotFoundException;
import atwoz.atwoz.match.command.domain.match.Match;
import atwoz.atwoz.match.command.domain.match.MatchRepository;
import atwoz.atwoz.match.command.domain.match.MatchStatus;
import atwoz.atwoz.match.command.domain.match.MatchType;
import atwoz.atwoz.match.command.domain.match.vo.Message;
import atwoz.atwoz.match.presentation.dto.MatchRequestDto;
import atwoz.atwoz.match.presentation.dto.MatchResponseDto;
import atwoz.atwoz.member.command.domain.introduction.MemberIntroduction;
import atwoz.atwoz.member.command.domain.introduction.MemberIntroductionCommandRepository;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchService {

    private static final String LOCK_PREFIX = "MATCH:";

    private final MemberCommandRepository memberCommandRepository;
    private final MemberIntroductionCommandRepository introductionCommandRepository;
    private final MatchRepository matchRepository;
    private final LockRepository lockRepository;

    @Transactional
    public void request(Long requesterId, MatchRequestDto request) {
        long responderId = request.responderId();
        String requesterName = findNickname(requesterId);
        MatchType matchType = getMatchType(requesterId, responderId);

        String key = generateKey(requesterId, responderId);
        lockRepository.withNamedLock(key, () -> {
            if (existsMutualMatch(requesterId, responderId)) {
                throw new ExistsMatchException();
            }

            Match match = Match.request(
                requesterId,
                responderId,
                Message.from(request.requestMessage()),
                requesterName,
                matchType
            );
            matchRepository.save(match);
        });
    }

    private MatchType getMatchType(Long requesterId, Long responderId) {
        Optional<MemberIntroduction> optionalIntroduction = introductionCommandRepository.findByMemberIdAndIntroducedMemberId(
            requesterId, responderId);
        if (optionalIntroduction.isEmpty()) {
            return MatchType.MATCH;
        }

        MemberIntroduction introduction = optionalIntroduction.get();
        if (introduction.getType().isSoulmateIntroduction()) {
            return MatchType.SOULMATE;
        }
        return MatchType.MATCH;
    }

    @Transactional
    public void approve(Long matchId, Long responderId, MatchResponseDto respondDto) {
        Match match = getWaitingMatchByIdAndResponderId(matchId, responderId);
        String responderName = findNickname(responderId);
        match.approve(Message.from(respondDto.responseMessage()), responderName);
    }

    @Transactional
    public void reject(Long matchId, Long responderId) {
        Match match = getWaitingMatchByIdAndResponderId(matchId, responderId);
        String responderName = findNickname(responderId);
        match.reject(responderName);
    }

    @Transactional
    public void rejectCheck(Long requesterId, Long matchId) {
        Match match = getRejectedMatchByIdAndRequesterId(matchId, requesterId);
        match.checkRejected();
    }

    private String findNickname(long memberId) {
        return memberCommandRepository.findById(memberId)
            .orElseThrow(() -> new EntityNotFoundException("Member not found. id: " + memberId))
            .getProfile()
            .getNickname()
            .getValue();
    }

    private boolean existsMutualMatch(Long requesterId, Long responderId) {
        return matchRepository.existsActiveMatchBetween(requesterId, responderId);
    }

    private String generateKey(Long requesterId, Long responderId) {
        return LOCK_PREFIX + Math.max(requesterId, responderId) + ":" + Math.min(requesterId, responderId);
    }

    private Match getWaitingMatchByIdAndResponderId(Long id, Long responderId) {
        Match match = matchRepository.findByIdAndResponderId(id, responderId)
            .orElseThrow(MatchNotFoundException::new);

        if (match.getStatus() != MatchStatus.WAITING) {
            throw new InvalidMatchUpdateException();
        }
        return match;
    }

    private Match getRejectedMatchByIdAndRequesterId(Long id, Long requesterId) {
        Match match = matchRepository.findByIdAndRequesterId(id, requesterId)
            .orElseThrow(MatchNotFoundException::new);

        if (match.getStatus() != MatchStatus.REJECTED) {
            throw new InvalidMatchUpdateException();
        }
        return match;
    }
}
