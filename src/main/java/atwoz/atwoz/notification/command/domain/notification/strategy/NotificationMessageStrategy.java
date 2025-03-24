package atwoz.atwoz.notification.command.domain.notification.strategy;

import atwoz.atwoz.notification.command.domain.notification.NotificationType;

public interface NotificationMessageStrategy {

    NotificationType getNotificationType();

    String generateTitle(String receiverName);

    String generateContent();
}