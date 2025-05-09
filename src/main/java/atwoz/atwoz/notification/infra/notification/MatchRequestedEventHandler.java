package atwoz.atwoz.notification.infra.notification;

import atwoz.atwoz.match.command.domain.match.event.MatchRequestedEvent;
import atwoz.atwoz.notification.command.application.notification.NotificationSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class MatchRequestedEventHandler {

    private final NotificationSendService notificationSendService;

    @Async
    @TransactionalEventListener(value = MatchRequestedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MatchRequestedEvent event) {
        NotificationRequest request = new NotificationRequest(
            event.getRequesterId(),
            "MEMBER",
            event.getResponderId(),
            "MATCH_REQUESTED"
        );
        notificationSendService.send(request);
    }
}
