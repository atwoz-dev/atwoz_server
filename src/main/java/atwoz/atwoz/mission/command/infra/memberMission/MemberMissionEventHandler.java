package atwoz.atwoz.mission.command.infra.memberMission;

import atwoz.atwoz.datingexam.application.dto.AllRequiredSubjectSubmittedEvent;
import atwoz.atwoz.interview.command.domain.answer.event.FirstInterviewSubmittedEvent;
import atwoz.atwoz.like.command.domain.LikeSentEvent;
import atwoz.atwoz.mission.command.application.memberMission.MemberMissionService;
import atwoz.atwoz.mission.command.domain.mission.ActionType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberMissionEventHandler {
    private final MemberMissionService memberMissionService;

    @Async
    @EventListener(value = AllRequiredSubjectSubmittedEvent.class)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AllRequiredSubjectSubmittedEvent event) {
        memberMissionService.executeMissionsByAction(event.getMemberId(), ActionType.FIRST_DATE_EXAM.name());
    }

    @Async
    @EventListener(value = LikeSentEvent.class)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LikeSentEvent event) {
        memberMissionService.executeMissionsByAction(event.getSenderId(), ActionType.LIKE.name());
    }

    @Async
    @EventListener(value = FirstInterviewSubmittedEvent.class)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(FirstInterviewSubmittedEvent event) {
        memberMissionService.executeMissionsByAction(event.getMemberId(), ActionType.INTERVIEW.name());
    }
}
