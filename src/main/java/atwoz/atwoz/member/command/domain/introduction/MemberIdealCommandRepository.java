package atwoz.atwoz.member.command.domain.introduction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberIdealCommandRepository extends JpaRepository<MemberIdeal, Long> {
    Optional<MemberIdeal> findByMemberId(Long memberId);
}
