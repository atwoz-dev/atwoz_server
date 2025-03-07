package atwoz.atwoz.notification.command.domain.notificationsetting;

import atwoz.atwoz.common.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "notification_settings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSetting extends SoftDeleteBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String deviceToken;

    private Boolean isOptedIn;

    private NotificationSetting(Long memberId, String deviceToken, Boolean isOptedIn) {
        this.memberId = memberId;
        this.deviceToken = deviceToken;
        this.isOptedIn = isOptedIn;
    }

    public static NotificationSetting of(long memberId) {
        return new NotificationSetting(memberId, null, false);
    }

    public void updateDeviceToken(String deviceToken) {
        setDeviceToken(deviceToken);
    }

    public void optIn() {
        setOptedIn(true);
    }

    public void optOut() {
        setOptedIn(false);
    }

    private void setDeviceToken(@NonNull String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void setOptedIn(boolean optedIn) {
        isOptedIn = optedIn;
    }
}
