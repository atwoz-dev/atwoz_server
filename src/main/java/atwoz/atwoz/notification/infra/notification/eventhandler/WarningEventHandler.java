package atwoz.atwoz.notification.infra.notification.eventhandler;

import atwoz.atwoz.admin.command.domain.warning.WarningIssuedEvent;
import atwoz.atwoz.notification.command.application.NotificationSendService;
import atwoz.atwoz.notification.infra.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class WarningEventHandler {

    private final NotificationSendService notificationSendService;

    @Async
    @TransactionalEventListener(value = WarningIssuedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WarningIssuedEvent event) {
        var request = new NotificationRequest(
            event.getAdminId(),
            "ADMIN",
            event.getMemberId(),
            "WARNING_SEND"
        );
        notificationSendService.send(request);
    }
}
