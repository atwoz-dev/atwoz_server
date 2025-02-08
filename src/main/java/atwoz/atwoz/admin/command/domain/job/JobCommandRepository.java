package atwoz.atwoz.admin.command.domain.job;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobCommandRepository extends JpaRepository<Job, Long> {
}
