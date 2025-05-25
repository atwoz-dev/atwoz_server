package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.NotificationType;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class NotificationTypeMapper {

    public static NotificationType toNotificationType(String notificationType) {
        try {
            return NotificationType.valueOf(notificationType.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidNotificationTypeException(notificationType);
        }
    }
}
