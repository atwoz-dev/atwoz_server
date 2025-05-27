package atwoz.atwoz.notification.command.domain;

import atwoz.atwoz.common.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.Map;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "notification_preferences")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationPreference extends SoftDeleteBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    private Long memberId;

    @Getter
    private boolean isEnabledGlobally = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "notification_types", joinColumns = @JoinColumn(name = "member_id"))
    @MapKeyEnumerated(STRING)
    @MapKeyColumn(name = "notification_type")
    @Column(name = "enabled")
    private Map<NotificationType, Boolean> isEnabledByNotificationType = new EnumMap<>(NotificationType.class);

    private NotificationPreference(Long memberId, Map<NotificationType, Boolean> isEnabledByNotificationType) {
        this.memberId = memberId;
        this.isEnabledByNotificationType = new EnumMap<>(isEnabledByNotificationType);
    }

    public static NotificationPreference of(long memberId) {
        Map<NotificationType, Boolean> defaults = new EnumMap<>(NotificationType.class);
        for (NotificationType type : NotificationType.values()) {
            defaults.put(type, true);
        }
        return new NotificationPreference(memberId, defaults);
    }

    public boolean canReceive(NotificationType type) {
        return isEnabledGlobally && isEnabledByNotificationType.getOrDefault(type, false);
    }

    public void enableGlobally() {
        isEnabledGlobally = true;
    }

    public void disableGlobally() {
        isEnabledGlobally = false;
    }

    public boolean isDisabledForType(NotificationType type) {
        return isEnabledByNotificationType.getOrDefault(type, false);
    }

    public void enableForNotificationType(NotificationType type) {
        isEnabledByNotificationType.put(type, true);
    }

    public void disableForNotificationType(NotificationType type) {
        isEnabledByNotificationType.put(type, false);
    }
}
