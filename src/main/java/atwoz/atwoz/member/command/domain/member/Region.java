package atwoz.atwoz.member.command.domain.member;

import atwoz.atwoz.member.command.domain.member.exception.InvalidMemberEnumValueException;
import lombok.Getter;

@Getter
public enum Region {
    SEOUL("서울"),
    DAEJEON("대전");

    private final String description;

    Region(String description) {
        this.description = description;
    }

    public static Region from(String value) {
        if (value == null || value.isEmpty()) return null;

        try {
            return Region.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMemberEnumValueException(value);
        }
    }

}
