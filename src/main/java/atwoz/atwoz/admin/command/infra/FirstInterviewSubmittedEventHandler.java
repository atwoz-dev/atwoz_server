package atwoz.atwoz.admin.command.infra;

import atwoz.atwoz.admin.command.application.memberscreening.FirstInterviewSubmittedEvent;
import atwoz.atwoz.admin.command.application.memberscreening.MemberScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class FirstInterviewSubmittedEventHandler {

    private final MemberScreeningService memberScreeningService;

    @Async
    @TransactionalEventListener(value = FirstInterviewSubmittedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(FirstInterviewSubmittedEvent event) {
        memberScreeningService.create(event.getMemberId());
    }
}
