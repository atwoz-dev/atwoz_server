package deepple.deepple.member.command.domain.member;

import deepple.deepple.member.command.domain.member.exception.InvalidMemberEnumValueException;
import lombok.Getter;

@Getter
public enum Grade {
    DIAMOND("다이아몬드"),
    GOLD("골드"),
    SILVER("실버");

    private final String description;

    Grade(String description) {
        this.description = description;
    }

    public static Grade from(String grade) {
        try {
            return Grade.valueOf(grade.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMemberEnumValueException("Invalid grade value: " + grade);
        }
    }
}