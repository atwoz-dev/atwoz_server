package atwoz.atwoz.notification.command.domain.notification;

public interface NotificationSender {

    void send(Notification notification, String receiverDeviceToken);
}
