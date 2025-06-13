package atwoz.atwoz.member.command.domain.introduction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface MemberIntroductionCommandRepository extends JpaRepository<MemberIntroduction, Long> {
    boolean existsByMemberIdAndIntroducedMemberId(long memberId, long introducedMemberId);

    Set<Long> findAllIntroducedMemberIdsByMemberIdAndInIntroducedMemberIds(long memberId,
        Set<Long> introducedMemberIds);
}
