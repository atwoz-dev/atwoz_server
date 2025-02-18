package atwoz.atwoz.admin.command.domain.screening;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreeningCommandRepository extends JpaRepository<Screening, Long> {

    boolean existsByMemberId(long memberId);
}
