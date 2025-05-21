package atwoz.atwoz.interview.command.domain.question;

import atwoz.atwoz.interview.command.domain.question.exception.InvalidInterviewCategoryException;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "인터뷰 질문 카테고리",
    example = "PERSONAL",
    type = "string"
)
public enum InterviewCategory {
    PERSONAL("나"),
    SOCIAL("관계"),
    ROMANTIC("연애");

    private String description;

    InterviewCategory(String description) {
        this.description = description;
    }

    public static InterviewCategory from(String value) {
        if (value == null) {
            return null;
        }

        try {
            return InterviewCategory.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInterviewCategoryException(value);
        }
    }
}
