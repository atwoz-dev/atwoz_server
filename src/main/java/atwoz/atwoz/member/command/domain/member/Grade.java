package atwoz.atwoz.member.command.domain.member;

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
}