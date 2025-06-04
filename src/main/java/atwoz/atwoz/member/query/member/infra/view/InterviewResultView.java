package atwoz.atwoz.member.query.member.infra.view;

import com.querydsl.core.annotations.QueryProjection;

public record InterviewResultView(
    String content,
    String category,
    String answer
) {
    @QueryProjection
    public InterviewResultView(String content, String category, String answer) {
        this.content = content;
        this.category = category;
        this.answer = answer;
    }
}
