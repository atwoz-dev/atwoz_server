package atwoz.atwoz.community.command.infra.selfintroduction;

import atwoz.atwoz.community.command.domain.selfintroduction.SelfIntroduction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelfIntroductionJpaRepository extends JpaRepository<SelfIntroduction, Long> {
}
