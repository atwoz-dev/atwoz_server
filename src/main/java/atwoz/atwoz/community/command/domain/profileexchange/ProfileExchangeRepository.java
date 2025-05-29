package atwoz.atwoz.community.command.domain.profileexchange;

import java.util.Optional;

public interface ProfileExchangeRepository {
    ProfileExchange save(ProfileExchange profileExchange);

    boolean existsProfileExchangeBetween(Long memberId, Long anotherMemberId);

    void withNamedLock(String key, Runnable runnable);

    Optional<ProfileExchange> findById(Long id);
}
