package atwoz.atwoz.member.command.infra.member;


import atwoz.atwoz.datingexam.application.dto.AllRequiredSubjectSubmittedEvent;
import atwoz.atwoz.member.command.application.member.MemberHeartBalanceService;
import atwoz.atwoz.member.command.application.member.MemberProfileService;
import atwoz.atwoz.mission.command.domain.memberMission.event.MemberMissionCompletedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEventHandler {
    private final MemberProfileService memberProfileService;
    private final MemberHeartBalanceService memberHeartBalanceService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AllRequiredSubjectSubmittedEvent event) {
        memberProfileService.markDatingExamSubmitted(event.getMemberId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberMissionCompletedEvent event) {
        memberHeartBalanceService.grantMissionHearts(
            event.getMemberId(),
            event.getRewardHeartAmount(),
            event.getActionType()
        );
    }
}
