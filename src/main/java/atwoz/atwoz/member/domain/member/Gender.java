package atwoz.atwoz.member.domain.member;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("남성"),
    WOMAN("여성");

    private final String description;

    Gender(String description) {
        this.description = description;
    }
}
