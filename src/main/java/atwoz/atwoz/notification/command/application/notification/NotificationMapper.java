package atwoz.atwoz.notification.command.application.notification;

import atwoz.atwoz.notification.command.domain.notification.Notification;
import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import atwoz.atwoz.notification.command.domain.notification.SenderType;
import atwoz.atwoz.notification.command.infra.notification.NotificationRequest;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class NotificationMapper {

    public static Notification toNotification(NotificationRequest request) {
        return Notification.builder()
                .senderId(request.senderId())
                .senderType(toSenderType(request.senderType()))
                .receiverId(request.receiverId())
                .type(toNotificationType(request.notificationType()))
                .build();
    }

    public static Notification toNotification(NotificationRequest request, String title, String content) {
        return Notification.builder()
                .senderId(request.senderId())
                .senderType(toSenderType(request.senderType()))
                .receiverId(request.receiverId())
                .type(toNotificationType(request.notificationType()))
                .title(title)
                .content(content)
                .build();
    }

    public static NotificationType toNotificationType(String notificationType) {
        return NotificationType.valueOf(notificationType.toUpperCase());
    }

    private static SenderType toSenderType(String senderType) {
        return SenderType.valueOf(senderType.toUpperCase());
    }
}
