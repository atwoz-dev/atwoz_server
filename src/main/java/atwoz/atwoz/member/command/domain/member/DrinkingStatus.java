package atwoz.atwoz.member.command.domain.member;

import atwoz.atwoz.member.command.domain.member.exception.InvalidMemberEnumValueException;
import lombok.Getter;

@Getter
public enum DrinkingStatus {
    NONE("전혀 마시지 않음"),
    QUIT("금주"),
    SOCIAL("사회적 음주"),
    OCCASIONAL("가끔 마심"),
    FREQUENT("술자리를 즐김");

    private final String description;

    DrinkingStatus(String description) {
        this.description = description;
    }

    public static DrinkingStatus from(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            return DrinkingStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMemberEnumValueException(value);
        }
    }
}
