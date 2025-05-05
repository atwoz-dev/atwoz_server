package atwoz.atwoz.like.query;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record LikeView(
    long likeId,
    long opponentId,
    String profileImageUrl,
    String nickname,
    String city,
    int yearOfBirth,
    boolean isMutualLike,
    LocalDateTime createdAt
) {
    @QueryProjection
    public LikeView {
    }
}