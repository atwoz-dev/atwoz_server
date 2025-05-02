package atwoz.atwoz.interview.query.question.view;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record AdminInterviewQuestionView(
    Long id,
    String content,
    String category,
    Boolean isPublic,
    LocalDateTime createdAt
) {
    @QueryProjection
    public AdminInterviewQuestionView {
    }
}
