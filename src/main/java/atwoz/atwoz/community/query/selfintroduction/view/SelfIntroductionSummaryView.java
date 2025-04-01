package atwoz.atwoz.community.query.selfintroduction.view;

import com.querydsl.core.annotations.QueryProjection;

public record SelfIntroductionSummaryView(
        Long id,
        String nickname,
        String profileUrl,
        Integer yearOfBirth,
        String title
) {
    @QueryProjection
    public SelfIntroductionSummaryView {
    }
}
