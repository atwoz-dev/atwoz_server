package atwoz.atwoz.mission.command.infra.memberMission;

import atwoz.atwoz.mission.command.domain.memberMission.MemberMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberMissionJpaRepository extends JpaRepository<MemberMission, Long> {
    Optional<MemberMission> findByMemberIdAndMissionId(Long memberId, Long missionId);
}
