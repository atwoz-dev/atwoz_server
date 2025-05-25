package atwoz.atwoz.notification.infra.notification.handler;

import atwoz.atwoz.admin.command.domain.warning.WarningIssuedEvent;
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
public class WarningIssuedEventHandler {

    private final NotificationSendService notificationSendService;

    @Async
    @TransactionalEventListener(value = WarningIssuedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WarningIssuedEvent event) {
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
}