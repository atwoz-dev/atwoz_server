package atwoz.atwoz.admin.command.domain.screening;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScreeningCommandRepository extends JpaRepository<Screening, Long> {

    boolean existsByMemberId(Long memberId);

    Optional<Screening> findByMemberId(Long memberId);
}
