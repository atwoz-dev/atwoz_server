package atwoz.atwoz.mission.command.infra.memberMission;

import atwoz.atwoz.mission.command.domain.memberMission.MemberMission;
import atwoz.atwoz.mission.command.domain.memberMission.MemberMissionCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberMissionCommandRepositoryImpl implements MemberMissionCommandRepository {

    private final MemberMissionJpaRepository memberMissionJpaRepository;

    @Override
    public Optional<MemberMission> findByMemberIdAndMissionId(final Long memberId, final Long missionId) {
        return memberMissionJpaRepository.findByMemberIdAndMissionId(memberId, missionId);
    }

    @Override
    public MemberMission save(MemberMission memberMission) {
        return memberMissionJpaRepository.save(memberMission);
    }
}
