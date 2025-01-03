package atwoz.atwoz.job.domain;

import java.util.Optional;

public interface JobRepository {
    boolean existsById(Long id);
}
