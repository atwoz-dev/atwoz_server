package atwoz.atwoz.notification.command.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(
    name = "device_registrations",
    uniqueConstraints = @UniqueConstraint(name = "uk_member_id", columnNames = "memberId")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DeviceRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    /**
     * 기기 고유 식별자
     */
    private String deviceId;

    /**
     * FCM(Firebase Cloud Messaging) 등록 토큰
     */
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

    public void refreshRegistrationToken(@NonNull String registrationToken) {
        this.registrationToken = registrationToken;
        isActive = true;
    }

    public void activate() {
        isActive = true;
    }

    public void deactivate() {
        isActive = false;
    }
}
