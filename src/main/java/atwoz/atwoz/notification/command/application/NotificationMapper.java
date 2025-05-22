package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.Notification;
import atwoz.atwoz.notification.command.domain.NotificationType;
import atwoz.atwoz.notification.command.domain.SenderType;
import atwoz.atwoz.notification.infra.notification.NotificationRequest;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class NotificationMapper {

    public static Notification toNotification(NotificationRequest request) {
        return Notification.create(
            request.senderId(),
            toSenderType(request.senderType()),
            request.receiverId(),
            toNotificationType(request.notificationType())
        );
    }

    private static NotificationType toNotificationType(String notificationType) {
        return NotificationType.valueOf(notificationType.toUpperCase());
    }

    private static SenderType toSenderType(String senderType) {
        return SenderType.valueOf(senderType.toUpperCase());
    }
}
