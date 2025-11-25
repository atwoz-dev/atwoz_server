package atwoz.atwoz.mission.command.infra.memberMission;

import atwoz.atwoz.datingexam.application.dto.AllRequiredSubjectSubmittedEvent;
import atwoz.atwoz.mission.command.application.memberMission.MemberMissionService;
import atwoz.atwoz.mission.command.domain.mission.ActionType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberMissionEventHandler {
    private final MemberMissionService memberMissionService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AllRequiredSubjectSubmittedEvent event) {
        memberMissionService.executeMissionsByAction(event.getMemberId(), ActionType.FIRST_DATE_EXAM.name());
    }
}
