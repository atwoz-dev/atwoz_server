package atwoz.atwoz.job.infra;

import atwoz.atwoz.job.command.domain.Job;
import atwoz.atwoz.job.command.domain.JobRepository;
import atwoz.atwoz.job.command.infra.JobJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JobRepositoryImpl implements JobRepository {

    private final JobJpaRepository jobJpaRepository;

    @Override
    public boolean existsById(Long id) {
        return jobJpaRepository.existsById(id);
    }

    @Override
    public Optional<Job> findById(Long id) {
        return jobJpaRepository.findById(id);
    }
}
