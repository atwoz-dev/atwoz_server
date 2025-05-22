package atwoz.atwoz.notification.command.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "notification_templates")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(STRING)
    @Column(columnDefinition = "varchar(50)")
    private NotificationType type;

    private String titleTemplate;

    private String bodyTemplate;

    private boolean isActive = true;

    private NotificationTemplate(NotificationType type, String titleTemplate, String bodyTemplate) {
        this.type = type;
        this.titleTemplate = titleTemplate;
        this.bodyTemplate = bodyTemplate;
    }

    public static NotificationTemplate of(NotificationType type, String titleTemplate, String bodyTemplate) {
        return new NotificationTemplate(type, titleTemplate, bodyTemplate);
    }

    public void validateBodyParams(Map<String, String> rawParams) {
        Set<String> keys = new HashSet<>();
        int idx = 0;
        while ((idx = bodyTemplate.indexOf('{', idx)) != -1) {
            int end = bodyTemplate.indexOf('}', idx);
            keys.add(bodyTemplate.substring(idx + 1, end));
            idx = end + 1;
        }
        if (!rawParams.keySet().containsAll(keys)) {
            throw new IllegalArgumentException("Missing template parameters: " + keys);
        }
    }

    public void updateTemplate(@NonNull String titleTemplate, @NonNull String bodyTemplate) {
        this.titleTemplate = titleTemplate;
        this.bodyTemplate = bodyTemplate;
    }

    public void updateTitleTemplate(@NonNull String titleTemplate) {
        this.titleTemplate = titleTemplate;
    }

    public void updateBodyTemplate(@NonNull String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }

    public void activate() {
        isActive = true;
    }

    public void deactivate() {
        isActive = false;
    }
}
