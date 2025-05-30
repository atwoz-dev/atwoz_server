package atwoz.atwoz.community.command.application.profileexchange;

import atwoz.atwoz.common.repository.LockRepository;
import atwoz.atwoz.community.command.application.profileexchange.exception.ProfileExchangeAlreadyExistsException;
import atwoz.atwoz.community.command.application.profileexchange.exception.ProfileExchangeNotFoundException;
import atwoz.atwoz.community.command.application.profileexchange.exception.ProfileExchangeResponderMismatchException;
import atwoz.atwoz.community.command.domain.profileexchange.ProfileExchange;
import atwoz.atwoz.community.command.domain.profileexchange.ProfileExchangeRepository;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileExchangeService {
    private static final String LOCK_PREFIX = "ProfileExchange:";
    private final ProfileExchangeRepository profileExchangeRepository;
    private final LockRepository lockRepository;
    private final MemberCommandRepository memberCommandRepository;

    @Transactional
    public void request(Long requesterId, Long responderId) {
        String key = generateKey(requesterId, responderId);
        lockRepository.withNamedLock(key, () -> {
            validateProfileExchangeRequest(requesterId, responderId);
            String senderName = getNickNameByMemberId(requesterId);
            ProfileExchange profileExchange = ProfileExchange.request(requesterId, responderId, senderName);
            profileExchangeRepository.save(profileExchange);
        });
    }

    @Transactional
    public void approve(Long profileExchangeId, Long responderId) {
        String senderName = getNickNameByMemberId(responderId);
        ProfileExchange profileExchange = getProfileExchangeById(profileExchangeId);
        validateProfileExchangeResponse(profileExchange, responderId);
        profileExchange.approve(senderName);
    }

    @Transactional
    public void reject(Long profileExchangeId, Long responderId) {
        String senderName = getNickNameByMemberId(responderId);
        ProfileExchange profileExchange = getProfileExchangeById(profileExchangeId);
        validateProfileExchangeResponse(profileExchange, responderId);
        profileExchange.reject(senderName);
    }

    private String getNickNameByMemberId(Long memberId) {
        return memberCommandRepository.findById(memberId).orElseThrow(MemberNotFoundException::new)
            .getProfile().getNickname().getValue();
    }

    private ProfileExchange getProfileExchangeById(Long profileExchangeId) {
        return profileExchangeRepository.findById(profileExchangeId).orElseThrow(ProfileExchangeNotFoundException::new);
    }

    private void validateProfileExchangeResponse(ProfileExchange profileExchange, Long responderId) {
        if (profileExchange.getResponderId() != responderId) {
            throw new ProfileExchangeResponderMismatchException();
        }
    }

    private String generateKey(Long requesterId, Long responderId) {
        return LOCK_PREFIX + Math.max(requesterId, responderId) + ":" + Math.min(requesterId, responderId);
    }

    private void validateProfileExchangeRequest(Long requesterId, Long responderId) {
        if (profileExchangeRepository.existsProfileExchangeBetween(requesterId, responderId)) {
            throw new ProfileExchangeAlreadyExistsException();
        }
    }
}
