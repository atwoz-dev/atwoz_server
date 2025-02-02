package atwoz.atwoz.admin.command.domain.memberscreening;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberScreeningCommandRepository extends JpaRepository<MemberScreening, Long> {

    boolean existsByMemberId(Long memberId);

    Optional<MemberScreening> findByMemberId(Long memberId);
}
