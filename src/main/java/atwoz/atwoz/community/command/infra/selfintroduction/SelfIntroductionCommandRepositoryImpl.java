package atwoz.atwoz.community.command.infra.selfintroduction;

import atwoz.atwoz.community.command.domain.selfintroduction.SelfIntroduction;
import atwoz.atwoz.community.command.domain.selfintroduction.SelfIntroductionCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SelfIntroductionCommandRepositoryImpl implements SelfIntroductionCommandRepository {

    private final SelfIntroductionJpaRepository selfIntroductionJpaRepository;

    @Override
    public void save(SelfIntroduction selfIntroduction) {
        selfIntroductionJpaRepository.save(selfIntroduction);
    }
}
