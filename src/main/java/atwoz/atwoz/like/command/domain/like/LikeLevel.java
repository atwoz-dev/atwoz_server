package atwoz.atwoz.like.command.domain.like;

import lombok.Getter;

@Getter
public enum LikeLevel {
    INTEREST("관심있어요"),
    VERY_INTEREST("매우 관심있어요");

    private final String description;

    LikeLevel(String description) {
        this.description = description;
    }
}
