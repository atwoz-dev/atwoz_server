package atwoz.atwoz.notification.command.application;

public class DeviceRegistrationNotFoundException extends RuntimeException {
    public DeviceRegistrationNotFoundException(long receiverId) {
        super("receiverId: " + receiverId + "에 대한 DeviceRegistration을 찾을 수 없습니다.");
    }
}
