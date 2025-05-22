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
    private boolean enabledGlobally = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "notification_types", joinColumns = @JoinColumn(name = "member_id"))
    @MapKeyEnumerated(STRING)
    @MapKeyColumn(name = "notification_type")
    @Column(name = "enabled")
    private Map<NotificationType, Boolean> enabledByNotificationType = new EnumMap<>(NotificationType.class);

    private NotificationPreference(Long memberId, Map<NotificationType, Boolean> enabledByNotificationType) {
        this.memberId = memberId;
        this.enabledByNotificationType = new EnumMap<>(enabledByNotificationType);
    }

    public static NotificationPreference of(long memberId) {
        Map<NotificationType, Boolean> defaults = new EnumMap<>(NotificationType.class);
        for (NotificationType type : NotificationType.values()) {
            defaults.put(type, true);
        }
        return new NotificationPreference(memberId, defaults);
    }

    public void enableGlobally() {
        enabledGlobally = true;
    }

    public void disableGlobally() {
        enabledGlobally = false;
    }

    public boolean isEnabledForType(NotificationType type) {
        return enabledByNotificationType.getOrDefault(type, true);
    }

    public void enableForNotificationType(NotificationType type) {
        enabledByNotificationType.put(type, true);
    }

    public boolean isDisabledForType(NotificationType type) {
        return enabledByNotificationType.getOrDefault(type, false);
    }

    public void disableForNotificationType(NotificationType type) {
        enabledByNotificationType.put(type, false);
    }
}
