package atwoz.atwoz.job.infra;

import atwoz.atwoz.job.domain.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JobRepositoryImpl implements JobRepository {

    private final JobJpaRepository jobJpaRepository;

    @Override
    public boolean existsById(Long id) {
        return jobJpaRepository.existsById(id);
    }
}
