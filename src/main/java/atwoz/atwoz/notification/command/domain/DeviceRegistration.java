package atwoz.atwoz.notification.command.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "device_registrations",
    uniqueConstraints = @UniqueConstraint(columnNames = {"memberId", "deviceId"}),
    indexes = {
        @Index(name = "idx_member_id_active", columnList = "memberId, isActive"),
        @Index(name = "idx_device_id", columnList = "deviceId")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DeviceRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String deviceId;

    private String registrationToken;

    private boolean isActive = true;

    private DeviceRegistration(Long memberId, String deviceId, String registrationToken) {
        this.memberId = memberId;
        this.deviceId = deviceId;
        this.registrationToken = registrationToken;
    }

    public static DeviceRegistration of(long memberId, String deviceId, String registrationToken) {
        return new DeviceRegistration(memberId, deviceId, registrationToken);
    }

    public void activate() {
        isActive = true;
    }

    public void deactivate() {
        isActive = false;
    }
}
