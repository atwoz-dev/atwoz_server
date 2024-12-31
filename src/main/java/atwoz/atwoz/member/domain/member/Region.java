package atwoz.atwoz.member.domain.member;

import atwoz.atwoz.member.exception.InvalidMemberEnumValueException;
import lombok.Getter;

@Getter
public enum Region {
    SEOUL("Seoul"), DAEJEON("Daejeon");

    private final String description;

    Region(String description) {
        this.description = description;
    }

    public static Region from(String value) {
        if (value == null) return null;
        try {
            return Region.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMemberEnumValueException(value);
        }
    }

}
