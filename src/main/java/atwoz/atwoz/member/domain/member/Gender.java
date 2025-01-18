package atwoz.atwoz.member.domain.member;

import atwoz.atwoz.member.domain.member.exception.InvalidMemberEnumValueException;
import lombok.Getter;

@Getter
public enum Gender {
    MALE("남성"),
    WOMAN("여성");

    private final String description;

    Gender(String description) {
        this.description = description;
    }

    public static Gender from(String value) {
        if (value == null) return null;

        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMemberEnumValueException(value);
        }
    }
}
