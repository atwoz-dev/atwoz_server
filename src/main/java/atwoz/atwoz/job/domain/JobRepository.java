package atwoz.atwoz.job.domain;

import java.util.Optional;

public interface JobRepository {
    Optional<Job> findById(Long id);
}
