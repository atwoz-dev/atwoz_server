package atwoz.atwoz.member.domain.member;

import atwoz.atwoz.member.exception.InvalidMemberEnumValueException;
import lombok.Getter;

@Getter
public enum SmokingStatus {
    NON_SMOKER("비흡연"),
    QUITTING("금연 중"),
    VAPING("전자담배"),
    OCCASIONAL_SMOKER("가끔 피움"),
    DAILY_SMOKER("매일 피움");

    private final String description;

    SmokingStatus(String description) {
        this.description = description;
    }

    public static SmokingStatus from(String value) {
        if (value == null) return null;

        try {
            return SmokingStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMemberEnumValueException(value);
        }
    }
}
