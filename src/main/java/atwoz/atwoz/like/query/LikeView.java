package atwoz.atwoz.like.query;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record LikeView(
    long id,
    String profileImageUrl,
    String nickname,
    String city,
    int yearOfBirth,
    LocalDateTime createdAt
) {
    @QueryProjection
    public LikeView {
    }
}