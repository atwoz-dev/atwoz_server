package atwoz.atwoz.notification.command.domain.notification.message;

import atwoz.atwoz.notification.command.domain.notification.NotificationType;

public interface MessageTemplate {

    NotificationType getNotificationType();

    String getTitle(MessageTemplateParameters parameters);

    String getContent(MessageTemplateParameters parameters);
}
