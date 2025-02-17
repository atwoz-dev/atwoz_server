package atwoz.atwoz.admin.command.infra;

import atwoz.atwoz.admin.command.application.screening.FirstInterviewSubmittedEvent;
import atwoz.atwoz.admin.command.application.screening.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class FirstInterviewSubmittedEventHandler {

    private final ScreeningService screeningService;

    @Async
    @TransactionalEventListener(value = FirstInterviewSubmittedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(FirstInterviewSubmittedEvent event) {
        screeningService.create(event.getMemberId());
    }
}
