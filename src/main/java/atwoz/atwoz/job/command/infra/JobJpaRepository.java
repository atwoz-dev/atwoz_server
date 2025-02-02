package atwoz.atwoz.job.command.infra;

import atwoz.atwoz.job.command.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobJpaRepository extends JpaRepository<Job, Long> {
}
