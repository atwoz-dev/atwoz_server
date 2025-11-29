package deepple.deepple.community.command.infra.profileexchange;

import deepple.deepple.community.command.domain.profileexchange.ProfileExchange;
import deepple.deepple.community.command.domain.profileexchange.ProfileExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProfileExchangeRepositoryImpl implements ProfileExchangeRepository {
    private final ProfileExchangeJpaRepository profileExchangeJpaRepository;

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