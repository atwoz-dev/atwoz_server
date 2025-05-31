package atwoz.atwoz.notification.infra;

import atwoz.atwoz.admin.command.domain.warning.WarningIssuedEvent;
import atwoz.atwoz.like.command.domain.LikeSentEvent;
import atwoz.atwoz.match.command.domain.match.event.MatchRequestedEvent;
import atwoz.atwoz.notification.command.application.NotificationSendRequest;
import atwoz.atwoz.notification.command.application.NotificationSendService;
import atwoz.atwoz.notification.command.domain.ChannelType;
import atwoz.atwoz.notification.command.domain.NotificationType;
import atwoz.atwoz.notification.command.domain.SenderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationSendService notificationSendService;

    @Async
    @TransactionalEventListener(value = MatchRequestedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handleMatchRequestedEvent(MatchRequestedEvent event) {
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

    @Async
    @TransactionalEventListener(value = WarningIssuedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handleWarningIssuedEvent(WarningIssuedEvent event) {
        var request = new NotificationSendRequest(
            SenderType.ADMIN,
            event.getAdminId(),
            event.getMemberId(),
            NotificationType.valueOf(event.getReasonType()),
            Map.of(),
            ChannelType.PUSH
        );
        notificationSendService.send(request);
    }

    @Async
    @TransactionalEventListener(value = LikeSentEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikeSentEvent(LikeSentEvent event) {
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