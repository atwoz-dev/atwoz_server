package atwoz.atwoz.member.query.member.infra.view;

import com.querydsl.core.annotations.QueryProjection;

public record InterviewInfoView(
    String title,
    String content
) {
    @QueryProjection
    public InterviewInfoView {
    }
}
