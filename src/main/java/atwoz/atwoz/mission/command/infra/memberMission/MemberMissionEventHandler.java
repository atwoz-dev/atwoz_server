package atwoz.atwoz.mission.command.infra.memberMission;

import atwoz.atwoz.interview.command.domain.answer.event.FirstInterviewSubmittedEvent;
import atwoz.atwoz.like.command.domain.LikeSentEvent;
import atwoz.atwoz.mission.command.application.memberMission.MemberMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberMissionEventHandler {
    private final MemberMissionService memberMissionService;

    /**
     * Notice : 미션의 항목이 추가될 때마다 해당 핸들링이 필요합니다.
     * 또는 공통 처리를 위한 이벤트 상속을 할 수 있을 것 같습니다.
     */
    @EventListener(value = LikeSentEvent.class)
    public void handle(LikeSentEvent event) {
        memberMissionService.executeMissionsByAction(event.getSenderId(), "LIKE");
    }

    @EventListener(value = FirstInterviewSubmittedEvent.class)
    public void handle(FirstInterviewSubmittedEvent event) {
        memberMissionService.executeMissionsByAction(event.getMemberId(), "INTERVIEW");
    }

    // TODO : 인호님 변경 사항에 맞추어서 모의고사 완료 이벤트 핸들링.
}
