package atwoz.atwoz.mission.command.domain.memberMission;

import java.util.Optional;

public interface MemberMissionCommandRepository {
    Optional<MemberMission> findByMemberIdAndMissionId(Long memberId, Long missionId);

    MemberMission save(MemberMission memberMission);

    Optional<MemberMission> findByMemberIdAndMissionIdOnToday(final Long memberId, final Long missionId);
}
