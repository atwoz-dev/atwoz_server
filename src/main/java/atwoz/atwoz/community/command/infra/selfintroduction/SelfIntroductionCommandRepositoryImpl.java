package atwoz.atwoz.community.command.infra.selfintroduction;

import atwoz.atwoz.community.command.domain.selfintroduction.SelfIntroduction;
import atwoz.atwoz.community.command.domain.selfintroduction.SelfIntroductionCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SelfIntroductionCommandRepositoryImpl implements SelfIntroductionCommandRepository {

    private final SelfIntroductionJpaRepository selfIntroductionJpaRepository;

    @Override
    public void save(SelfIntroduction selfIntroduction) {
        selfIntroductionJpaRepository.save(selfIntroduction);

    }

    @Override
    public Optional<SelfIntroduction> findById(Long id) {
        return selfIntroductionJpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        selfIntroductionJpaRepository.deleteById(id);
    }
}
