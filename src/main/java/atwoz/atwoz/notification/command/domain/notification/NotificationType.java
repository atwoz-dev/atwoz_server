package atwoz.atwoz.notification.command.domain.notification;

import lombok.Getter;

import static atwoz.atwoz.notification.command.domain.notification.NotificationCategory.*;

@Getter
public enum NotificationType {
    MATCH_REQUESTED(SOCIAL, "MATCH_REQUESTED", "매치 요청"),
    MATCH_ACCEPTED(SOCIAL, "MATCH_ACCEPTED", "매치 수락"),
    MATCH_REJECTED(SOCIAL, "MATCH_REJECTED", "매치 거절"),

    LIKE_SENT(SOCIAL, "LIKE_SENT", "좋아요 전송"),

    PROFILE_EXCHANGE_REQUESTED(SOCIAL, "PROFILE_EXCHANGE_REQUESTED", "프로필 교환 요청"),
    PROFILE_EXCHANGE_ACCEPTED(SOCIAL, "PROFILE_EXCHANGE_ACCEPTED", "프로필 교환 수락"),
    PROFILE_EXCHANGE_REJECTED(SOCIAL, "PROFILE_EXCHANGE_REJECTED", "프로필 교환 거절"),

    PROFILE_IMAGE_CHANGE_REQUESTED(ADMIN, "PROFILE_IMAGE_CHANGE_REQUESTED", "프로필 이미지 변경 요청"),
    INAPPROPRIATE_CONTENT(ADMIN, "INAPPROPRIATE_CONTENT", "게시글에 부적절한 내용 포함"),
    WARNING_ISSUED(ADMIN, "WARNING_ISSUED", "경고 발행"),
    WARNING_THRESHOLD_EXCEEDED(ADMIN, "WARNING_THRESHOLD_EXCEEDED", "경고 횟수 누적"),

    INTERVIEW_REQUESTED(ACTION, "INTERVIEW_REQUESTED", "인터뷰 작성 요청"),
    LOGIN_REQUESTED(ACTION, "LOGIN_REQUESTED", "장기간 미접속 시 접속 요청"),

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
