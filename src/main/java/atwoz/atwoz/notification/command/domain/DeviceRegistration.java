package atwoz.atwoz.notification.command.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "device_registrations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String deviceId;

    @Getter
    private String registrationToken;

    @Getter
    private boolean isActive = true;

    private DeviceRegistration(Long memberId, String deviceId, String registrationToken) {
        this.memberId = memberId;
        this.deviceId = deviceId;
        this.registrationToken = registrationToken;
    }

    public static DeviceRegistration of(long userId, String deviceId, String registrationToken) {
        return new DeviceRegistration(userId, deviceId, registrationToken);
    }

    public void activate() {
        isActive = true;
    }

    public void deactivate() {
        isActive = false;
    }
}
