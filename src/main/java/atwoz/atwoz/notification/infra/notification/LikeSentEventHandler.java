package atwoz.atwoz.notification.infra.notification;

import atwoz.atwoz.like.command.domain.LikeSentEvent;
import atwoz.atwoz.notification.command.application.notification.NotificationSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class LikeSentEventHandler {
    private final NotificationSendService notificationSendService;

    @Async
    @TransactionalEventListener(value = LikeSentEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LikeSentEvent event) {
        var request = new NotificationRequest(event.getSenderId(), "MEMBER", event.getReceiverId(),
            "LIKE_SENT");
        notificationSendService.send(request);
    }
}
