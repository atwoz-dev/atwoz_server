package atwoz.atwoz.member.query.member.view;

import com.querydsl.core.annotations.QueryProjection;

public record InterviewInfoView(
    String title,
    String content
) {
    @QueryProjection
    public InterviewInfoView {
    }
}
