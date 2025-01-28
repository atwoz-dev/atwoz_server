package atwoz.atwoz.job.command.infra;

import atwoz.atwoz.job.command.domain.JobCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JobCommandRepositoryImpl implements JobCommandRepository {

    private final JobJpaRepository jobJpaRepository;

    @Override
    public boolean existsById(Long id) {
        return jobJpaRepository.existsById(id);
    }
}
