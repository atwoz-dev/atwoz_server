package atwoz.atwoz.community.command.domain.selfintroduction;

import java.util.Optional;

public interface SelfIntroductionCommandRepository {
    void save(SelfIntroduction selfIntroduction);

    Optional<SelfIntroduction> findById(Long id);

    void deleteById(Long id);
}
