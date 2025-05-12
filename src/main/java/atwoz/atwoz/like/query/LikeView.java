package atwoz.atwoz.like.query;

import java.time.LocalDateTime;

public record LikeView(
    long likeId,
    long opponentId,
    String profileImageUrl,
    String nickname,
    String city,
    int age,
    boolean isMutualLike,
    LocalDateTime createdAt
) {
}