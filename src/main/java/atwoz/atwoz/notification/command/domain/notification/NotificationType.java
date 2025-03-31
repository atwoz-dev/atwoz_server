package atwoz.atwoz.notification.command.domain.notification;

import lombok.Getter;

import static atwoz.atwoz.notification.command.domain.notification.NotificationCategory.*;

@Getter
public enum NotificationType {
    MATCH_REQUESTED(SOCIAL, "MATCH_REQUESTED", "매치 요청"),
    MATCH_COMPLETED(SOCIAL, "MATCH_COMPLETED", "매치 완료"),

    PROFILE_IMAGE(ACTION, "PROFILE_IMAGE", "프로필 이미지 변경 요청"),

    INAPPROPRIATE_CONTENT(ADMIN, "INAPPROPRIATE_CONTENT", "부적절한 내용 포함"),

    NONE(ETC, "NONE", "NONE");

    private final NotificationCategory category;
    private final String code;
    private final String description;

    NotificationType(NotificationCategory category, String code, String description) {
        this.category = category;
        this.code = code;
        this.description = description;
    }

    public boolean isSocial() {
        return category == SOCIAL;
    }
}
