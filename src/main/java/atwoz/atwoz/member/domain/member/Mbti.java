package atwoz.atwoz.member.domain.member;

import lombok.Getter;

@Getter
public enum Mbti {
    ESFP("ESFP"), ESFJ("ESFJ"), ESTP("ESTP"), ESTJ("ESTJ"),
    ENFP("ENFP"), ENFJ("ENFJ"), ENTP("ENTP"), ENTJ("ENTJ"),
    ISFP("ISFP"), ISFJ("ISFJ"), ISTP("ISTP"), ISTJ("ISTJ"),
    INFP("INFP"), INFJ("INFJ"), INTP("INTP"), INTJ("INTJ");

    private final String description;

    Mbti(String description) {
        this.description = description;
    }
}
