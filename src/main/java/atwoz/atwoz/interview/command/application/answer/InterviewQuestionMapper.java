package atwoz.atwoz.interview.command.application.answer;

import atwoz.atwoz.interview.command.application.question.exception.InvalidInterviewCategoryException;
import atwoz.atwoz.interview.command.domain.question.InterviewCategory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InterviewQuestionMapper {

    public static InterviewCategory toInterviewCategory(String categoryString) {
        try {
            return InterviewCategory.valueOf(categoryString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInterviewCategoryException();
        }
    }
}
