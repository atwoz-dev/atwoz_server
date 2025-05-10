package atwoz.atwoz.like.command.application;

import atwoz.atwoz.like.command.domain.LikeLevel;
import atwoz.atwoz.like.presentation.LikeLevelRequest;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class LikeMapper {

    public static LikeLevel toLikeLevel(LikeLevelRequest likeLevelRequest) {
        try {
            return switch (likeLevelRequest) {
                case INTERESTED -> LikeLevel.INTERESTED;
                case HIGHLY_INTERESTED -> LikeLevel.HIGHLY_INTERESTED;
            };
        } catch (IllegalArgumentException e) {
            throw new InvalidLikeLevelException(likeLevelRequest.toString());
        }
    }
}
