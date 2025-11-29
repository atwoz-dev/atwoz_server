package deepple.deepple.admin.command.domain.screening;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScreeningCommandRepository extends JpaRepository<Screening, Long> {

    boolean existsByMemberId(long memberId);

    Optional<Screening> findByMemberId(Long memberId);
}
