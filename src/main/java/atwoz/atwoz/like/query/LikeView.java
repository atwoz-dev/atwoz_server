package atwoz.atwoz.like.query;

import atwoz.atwoz.member.command.domain.member.City;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record LikeView(
    long likeId,
    long opponentId,
    String profileImageUrl,
    String nickname,
    @Schema(implementation = City.class)
    String city,
    int age,
    boolean isMutualLike,
    LocalDateTime createdAt
) {
}