package atwoz.atwoz.like.command.domain.like;

import lombok.Getter;

@Getter
public enum LikeLevel {
    INTERESTED("관심있어요"),
    HIGHLY_INTERESTED("매우 관심있어요");

    private final String description;

    LikeLevel(String description) {
        this.description = description;
    }
}
