package atwoz.atwoz.member.domain.member;

import lombok.Getter;

@Getter
public enum DrinkingStatus {
    ;

    private String description;

    DrinkingStatus(String description) {
        this.description = description;
    }
}
