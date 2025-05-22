package atwoz.atwoz.notification.command.domain;

public interface NotificationSender {

    void send(Notification notification, String receiverDeviceToken);
}
