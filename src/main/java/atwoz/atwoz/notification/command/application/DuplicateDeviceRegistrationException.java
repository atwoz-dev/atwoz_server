package atwoz.atwoz.notification.command.application;

public class DuplicateDeviceRegistrationException extends RuntimeException {
    public DuplicateDeviceRegistrationException(String deviceId) {
        super("이미 등록된 디바이스입니다: " + deviceId);
    }
}