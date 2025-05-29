package atwoz.atwoz.community.command.application.profileexchange;

import atwoz.atwoz.community.command.application.profileexchange.exception.ProfileExchangeAlreadyExists;
import atwoz.atwoz.community.command.application.profileexchange.exception.ProfileExchangeNotFoundException;
import atwoz.atwoz.community.command.application.profileexchange.exception.ProfileExchangeResponderMismatchException;
import atwoz.atwoz.community.command.domain.profileexchange.ProfileExchange;
import atwoz.atwoz.community.command.domain.profileexchange.ProfileExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileExchangeService {
    private final ProfileExchangeRepository profileExchangeRepository;

    @Transactional
    public void request(Long requesterId, Long responderId) {
        String key = generateKey(requesterId, responderId);
        profileExchangeRepository.withNamedLock(key, () -> {
            validateProfileExchangeRequest(requesterId, responderId);
            ProfileExchange profileExchange = ProfileExchange.request(requesterId, responderId);
            profileExchangeRepository.save(profileExchange);
        });
    }

    @Transactional
    public void approve(Long profileExchangeId, Long responderId) {
        ProfileExchange profileExchange = getProfileExchangeById(profileExchangeId);
        validateProfileExchangeResponse(profileExchange, responderId);
        profileExchange.approve();
    }

    @Transactional
    public void reject(Long profileExchangeId, Long responderId) {
        ProfileExchange profileExchange = getProfileExchangeById(profileExchangeId);
        validateProfileExchangeResponse(profileExchange, responderId);
        profileExchange.reject();
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
        return Math.max(requesterId, responderId) + ":" + Math.min(requesterId, responderId);
    }

    private void validateProfileExchangeRequest(Long requesterId, Long responderId) {
        if (profileExchangeRepository.existsProfileExchangeBetween(requesterId, responderId)) {
            throw new ProfileExchangeAlreadyExists();
        }
    }
}
