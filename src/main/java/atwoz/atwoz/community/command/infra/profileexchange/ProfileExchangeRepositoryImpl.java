package atwoz.atwoz.community.command.infra.profileexchange;

import atwoz.atwoz.common.repository.LockRepository;
import atwoz.atwoz.community.command.domain.profileexchange.ProfileExchange;
import atwoz.atwoz.community.command.domain.profileexchange.ProfileExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProfileExchangeRepositoryImpl implements ProfileExchangeRepository {
    private final ProfileExchangeJpaRepository profileExchangeJpaRepository;
    private final LockRepository lockRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProfileExchange save(final ProfileExchange profileExchange) {
        return profileExchangeJpaRepository.save(profileExchange);
    }

    @Override
    public boolean existsProfileExchangeBetween(final Long memberId, final Long anotherMemberId) {
        return profileExchangeJpaRepository.existsProfileExchangeBetween(memberId, anotherMemberId);
    }

    @Override
    public Optional<ProfileExchange> findById(final Long id) {
        return profileExchangeJpaRepository.findById(id);
    }
}