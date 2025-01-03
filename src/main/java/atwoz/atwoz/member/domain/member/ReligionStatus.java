package atwoz.atwoz.member.domain.member;

import atwoz.atwoz.member.exception.InvalidMemberEnumValueException;
import lombok.Getter;

@Getter
public enum ReligionStatus {
    NO_RELIGION("무교"),
    CHRISTIANITY("기독교"),
    CATHOLIC("천주교"),
    BUDDHISM("불교"),
    OTHER("기타");

    private String description;

    ReligionStatus(String description) {
        this.description = description;
    }

    public static ReligionStatus from(String value) {
        if (value == null) return null;
        try {
            return ReligionStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMemberEnumValueException(value);
        }
    }
}
