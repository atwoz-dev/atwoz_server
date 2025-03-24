package atwoz.atwoz.member.command.domain.introduction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberIntroductionCommandRepository extends JpaRepository<MemberIntroduction, Long> {
    boolean existsByMemberIdAndIntroducedMemberId(long memberId, long introducedMemberId);
}
