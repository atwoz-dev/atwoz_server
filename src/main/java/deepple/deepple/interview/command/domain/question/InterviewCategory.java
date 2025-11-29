package deepple.deepple.interview.command.domain.question;

import deepple.deepple.interview.command.domain.question.exception.InvalidInterviewCategoryException;

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
