package atwoz.atwoz.interview.query.question.view;

import atwoz.atwoz.interview.command.domain.question.InterviewCategory;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

public record InterviewQuestionView(
    Long questionId,
    String questionContent,
    @Schema(implementation = InterviewCategory.class)
    String category,
    boolean isAnswered,
    Long answerId,
    String answerContent
) {
    @QueryProjection
    public InterviewQuestionView {
    }
}
