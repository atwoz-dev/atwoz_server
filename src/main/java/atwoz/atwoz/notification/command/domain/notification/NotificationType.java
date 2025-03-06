package atwoz.atwoz.notification.command.domain.notification;

import lombok.Getter;

@Getter
public enum NotificationType {
    MATCH_REQUESTED("매치 요청"),
    MATCH_COMPLETED("매치 완료");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }
}
