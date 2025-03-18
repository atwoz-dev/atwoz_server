package atwoz.atwoz.member.command.domain.member;

import atwoz.atwoz.member.command.domain.member.exception.InvalidMemberEnumValueException;
import lombok.Getter;

@Getter
public enum Religion {
    NONE("무교"),
    CHRISTIAN("기독교"),
    CATHOLIC("천주교"),
    BUDDHIST("불교"),
    OTHER("기타");

    private final String description;

    Religion(String description) {
        this.description = description;
    }

    public static Religion from(String value) {
        if (value == null || value.isEmpty()) return null;

        try {
            return Religion.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMemberEnumValueException(value);
        }
    }
}
