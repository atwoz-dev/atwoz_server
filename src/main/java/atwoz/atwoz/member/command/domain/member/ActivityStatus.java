package atwoz.atwoz.member.command.domain.member;

import atwoz.atwoz.member.command.domain.member.exception.InvalidMemberEnumValueException;
import lombok.Getter;

@Getter
public enum ActivityStatus {
    ACTIVE("활동중"),
    SUSPENDED_TEMPORARILY("일시 정지"),
    SUSPENDED_PERMANENTLY("영구 정지"),
    DORMANT("휴면");

    private final String description;

    ActivityStatus(String description) {
        this.description = description;
    }

    public static ActivityStatus from(String activityStatus) {
        try {
            return ActivityStatus.valueOf(activityStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMemberEnumValueException("Invalid activity status: " + activityStatus);
        }
    }
}