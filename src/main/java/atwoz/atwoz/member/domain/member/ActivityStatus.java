package atwoz.atwoz.member.domain.member;

import lombok.Getter;

@Getter
public enum ActivityStatus {
    ACTIVE("활동중"),
    BANNED("영구 정지"),
    SUSPENDED("일시 정지"),
    DORMANT("휴면");

    private final String description;

    ActivityStatus(String description) {
        this.description = description;
    }
}
