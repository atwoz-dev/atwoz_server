package atwoz.atwoz.notification.command.infra.notification;

public record NotificationRequest(
        long senderId,
        String senderType,
        long receiverId,
        String notificationType
) {
}
