package atwoz.atwoz.notification.infra.handler;

import atwoz.atwoz.match.command.domain.match.event.MatchRequestedEvent;
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
public class MatchRequestedEventHandler {

    private final NotificationSendService notificationSendService;

    @Async
    @TransactionalEventListener(value = MatchRequestedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MatchRequestedEvent event) {
        NotificationSendRequest request = new NotificationSendRequest(
            SenderType.MEMBER,
            event.getRequesterId(),
            event.getResponderId(),
            NotificationType.MATCH_REQUEST,
            Map.of("senderName", event.getRequesterName()),
            ChannelType.PUSH
        );
        notificationSendService.send(request);
    }
}
