package atwoz.atwoz.job.infra;

import atwoz.atwoz.job.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobJpaRepository extends JpaRepository<Job, Long> {
}
