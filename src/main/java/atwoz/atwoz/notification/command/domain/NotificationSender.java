package atwoz.atwoz.notification.command.domain;

public interface NotificationSender {

    ChannelType channel();

    void send(Notification notification, DeviceRegistration deviceRegistration) throws Exception;
}
