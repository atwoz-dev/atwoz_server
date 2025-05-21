package atwoz.atwoz.interview.query.question.view;

import atwoz.atwoz.interview.command.domain.question.InterviewCategory;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record AdminInterviewQuestionView(
    Long id,
    String content,
    @Schema(implementation = InterviewCategory.class)
    String category,
    Boolean isPublic,
    LocalDateTime createdAt
) {
    @QueryProjection
    public AdminInterviewQuestionView {
    }
}
