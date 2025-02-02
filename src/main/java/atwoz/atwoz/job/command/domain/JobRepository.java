package atwoz.atwoz.job.command.domain;

import java.util.Optional;

public interface JobRepository {
    boolean existsById(Long id);

    Optional<Job> findById(Long id);
}
