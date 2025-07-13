package atwoz.atwoz.mission.command.application.memberMission;

import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.mission.command.application.memberMission.exception.MemberNotFoundException;
import atwoz.atwoz.mission.command.domain.memberMission.MemberMission;
import atwoz.atwoz.mission.command.domain.memberMission.MemberMissionCommandRepository;
import atwoz.atwoz.mission.command.domain.mission.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberMissionService {
    private final MemberMissionCommandRepository memberMissionCommandRepository;
    private final MissionCommandRepository missionCommandRepository;
    private final MemberCommandRepository memberCommandRepository;

    @Transactional
    public void createOrUpdate(Long memberId, String actionType) {
        Member member = getMember(memberId);
        List<Mission> missions = getMission(member.getProfile().getGender().name(), actionType);

        missions.forEach(mission -> {
            MemberMission memberMission =
                mission.getFrequencyType() == FrequencyType.CHALLENGE ? createOrFindByMemberIdAndMissionId(memberId,
                    mission.getId()) : createOrFindByMemberIdAndMissionIdOnToday(memberId, mission.getId());
            if (!memberMission.isCompleted()) {
                memberMission.countPlus(mission.getRequiredAttempt(), mission.getRepeatableCount());
            }
        });
    }

    private List<Mission> getMission(String actionType, String targetGender) {
        return missionCommandRepository.findByActionTypeAndTargetGender(ActionType.from(actionType),
            TargetGender.from(targetGender));
    }

    private Member getMember(Long memberId) {
        return memberCommandRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    private MemberMission createOrFindByMemberIdAndMissionId(Long memberId, Long missionId) {
        return memberMissionCommandRepository.findByMemberIdAndMissionId(memberId, missionId)
            .orElseGet(() -> create(memberId, missionId));
    }

    private MemberMission createOrFindByMemberIdAndMissionIdOnToday(Long memberId, Long missionId) {
        return memberMissionCommandRepository.findByMemberIdAndMissionIdOnToday(memberId, missionId)
            .orElseGet(() -> create(memberId, missionId));
    }

    private MemberMission create(Long memberId, Long missionId) {
        return memberMissionCommandRepository.save(MemberMission.create(memberId, missionId));
    }
}
