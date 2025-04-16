package atwoz.atwoz.interview.query.question.view;

import com.querydsl.core.annotations.QueryProjection;

public record InterviewQuestionView(
        Long questionId,
        String questionContent,
        String category,
        boolean isAnswered,
        Long answerId,
        String answerContent
) {
    @QueryProjection
    public InterviewQuestionView {
    }
}
