package atwoz.atwoz.mission.command.application.memberMission;

import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import atwoz.atwoz.mission.command.application.memberMission.exception.MemberNotFoundException;
import atwoz.atwoz.mission.command.domain.memberMission.MemberMissionCommandRepository;
import atwoz.atwoz.mission.command.domain.mission.ActionType;
import atwoz.atwoz.mission.command.domain.mission.Mission;
import atwoz.atwoz.mission.command.domain.mission.MissionCommandRepository;
import atwoz.atwoz.mission.command.domain.mission.TargetGender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberMissionService {
    private final MemberMissionCommandRepository memberMissionCommandRepository;
    private final MissionCommandRepository missionCommandRepository;
    private final MemberCommandRepository memberCommandRepository;

    public void createOrUpdate(Long memberId, String actionType) {
        Member member = getMember(memberId);
        List<Mission> missions = getMission(member.getProfile().getGender().name(), actionType);

        missions.forEach(mission -> {

        });
    }


    private List<Mission> getMission(String actionType, String targetGender) {
        return missionCommandRepository.findByActionTypeAndTargetGender(ActionType.from(actionType),
            TargetGender.from(targetGender));
    }

    private Member getMember(Long memberId) {
        return memberCommandRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
    }
}
