package atwoz.atwoz.member.command.domain.member;

import atwoz.atwoz.member.command.domain.member.exception.InvalidMemberEnumValueException;
import lombok.Getter;

@Getter
public enum SmokingStatus {
    NONE("비흡연"),
    QUIT("금연"),
    OCCASIONAL("가끔 피움"),
    DAILY("매일 피움"),
    VAPE("전자담배");

    private final String description;

    SmokingStatus(String description) {
        this.description = description;
    }

    public static SmokingStatus from(String value) {
        if (value == null || value.isEmpty()) return null;

        try {
            return SmokingStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMemberEnumValueException(value);
        }
    }
}
