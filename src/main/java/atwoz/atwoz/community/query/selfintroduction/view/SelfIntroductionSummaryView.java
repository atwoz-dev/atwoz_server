package atwoz.atwoz.community.query.selfintroduction.view;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record SelfIntroductionSummaryView(
    Long id,
    String nickname,
    String profileUrl,
    Integer yearOfBirth,
    String title,
    String content,
    LocalDateTime createdAt
) {
    @QueryProjection
    public SelfIntroductionSummaryView {
    }
}
