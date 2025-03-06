package atwoz.atwoz.notification.command.domain.notificationsetting;

import atwoz.atwoz.common.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_settings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSetting extends SoftDeleteBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private Long deviceId;

    private Boolean isOptedIn;

    private NotificationSetting(Long memberId, Long deviceId, Boolean isOptedIn) {
        this.memberId = memberId;
        this.deviceId = deviceId;
        this.isOptedIn = isOptedIn;
    }

    public static NotificationSetting of(long memberId, long deviceId, boolean isOptedIn) {
        return new NotificationSetting(memberId, deviceId, isOptedIn);
    }
}
