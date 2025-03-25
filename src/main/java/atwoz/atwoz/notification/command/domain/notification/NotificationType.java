package atwoz.atwoz.notification.command.domain.notification;

import lombok.Getter;

@Getter
public enum NotificationType {
    SOCIAL_MATCH_REQUESTED("매치 요청"),
    SOCIAL_MATCH_COMPLETED("매치 완료"),

    ACTION_PROFILE_IMAGE("프로필 이미지 변경 요청"),

    ADMIN_INAPPROPRIATE_CONTENT("부적절한 내용 포함");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }
}
