package atwoz.atwoz.notification.infra.handler;

import atwoz.atwoz.like.command.domain.LikeSentEvent;
import atwoz.atwoz.notification.command.application.NotificationSendRequest;
import atwoz.atwoz.notification.command.application.NotificationSendService;
import atwoz.atwoz.notification.command.domain.ChannelType;
import atwoz.atwoz.notification.command.domain.NotificationType;
import atwoz.atwoz.notification.command.domain.SenderType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LikeSentEventHandler {

    private final NotificationSendService notificationSendService;

    @Async
    @TransactionalEventListener(value = LikeSentEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LikeSentEvent event) {
        var request = new NotificationSendRequest(
            SenderType.MEMBER,
            event.getSenderId(),
            event.getReceiverId(),
            NotificationType.LIKE,
            Map.of("senderName", event.getSenderName()),
            ChannelType.PUSH
        );
        notificationSendService.send(request);
    }
}
