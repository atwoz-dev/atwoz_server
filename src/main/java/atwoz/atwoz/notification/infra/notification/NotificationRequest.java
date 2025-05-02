package atwoz.atwoz.notification.infra.notification;

public record NotificationRequest(
    long senderId,
    String senderType,
    long receiverId,
    String notificationType
) {
}
