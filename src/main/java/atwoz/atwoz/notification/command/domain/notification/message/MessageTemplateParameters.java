package atwoz.atwoz.notification.command.domain.notification.message;

import atwoz.atwoz.notification.command.domain.notification.NotificationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageTemplateParameters {
    private final NotificationType notificationType;
    private final String receiverName;

    public static MessageTemplateParameters of(NotificationType notificationType, String receiverName) {
        return new MessageTemplateParameters(notificationType, receiverName);
    }

    public static MessageTemplateParameters from(NotificationType notificationType) {
        return new MessageTemplateParameters(notificationType, null);
    }
}
