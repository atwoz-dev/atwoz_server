package atwoz.atwoz.interview.command.domain.question;

import atwoz.atwoz.interview.command.domain.question.exception.InvalidInterviewQuestionContentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterviewQuestionTest {

    @Nested
    @DisplayName("of 메서드 테스트")
    class ofMethodTest {
        @ParameterizedTest
        @ValueSource(strings = {"content is null", "interviewCategory is null"})
        @DisplayName("of 메서드에서 필드 값이 null이면 예외를 던집니다.")
        void throwsExceptionWhenFieldValueIsNull(String fieldName) {
            // given
            String content = fieldName.equals("content is null") ? null : "content";
            InterviewCategory interviewCategory = fieldName.equals("interviewCategory is null") ? null : InterviewCategory.PERSONAL;
            boolean isPublic = true;

            // when & then
            assertThatThrownBy(() -> InterviewQuestion.of(content, interviewCategory, isPublic))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("of 메서드에서 content가 blank이면 예외를 던집니다.")
        void throwsExceptionWhenContentIsBlank() {
            // given
            String content = " ";
            InterviewCategory interviewCategory = InterviewCategory.PERSONAL;
            boolean isPublic = true;

            // when & then
            assertThatThrownBy(() -> InterviewQuestion.of(content, interviewCategory, isPublic))
                    .isInstanceOf(InvalidInterviewQuestionContentException.class);
        }

        @Test
        @DisplayName("of 메서드에서 필드 값이 정상이면 InterviewQuestion 객체를 생성합니다.")
        void createInterviewQuestionObjectWhenFieldValuesAreValid() {
            // given
            String content = "content";
            InterviewCategory interviewCategory = InterviewCategory.PERSONAL;
            boolean isPublic = true;

            // when
            InterviewQuestion interviewQuestion = InterviewQuestion.of(content, interviewCategory, isPublic);

            // then
            assertThat(interviewQuestion).isNotNull();
        }
    }

    @Nested
    @DisplayName("update 메서드 테스트")
    class updateMethodTest {

        @ParameterizedTest
        @ValueSource(strings = {"content is null", "interviewCategory is null"})
        @DisplayName("update 메서드에서 필드 값이 null이면 예외를 던집니다.")
        void throwsExceptionWhenFieldValueIsNull(String fieldName) {
            // given
            InterviewQuestion interviewQuestion = InterviewQuestion.of("content", InterviewCategory.PERSONAL, true);
            String content = fieldName.equals("content is null") ? null : "updated content";
            InterviewCategory interviewCategory = fieldName.equals("interviewCategory is null") ? null : InterviewCategory.SOCIAL;
            boolean isPublic = false;

            // when & then
            assertThatThrownBy(() -> interviewQuestion.update(content, interviewCategory, isPublic))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("update 메서드에서 content가 blank이면 예외를 던집니다.")
        void throwsExceptionWhenContentIsBlank() {
            // given
            InterviewQuestion interviewQuestion = InterviewQuestion.of("content", InterviewCategory.PERSONAL, true);
            String content = " ";
            InterviewCategory interviewCategory = InterviewCategory.SOCIAL;
            boolean isPublic = false;

            // when & then
            assertThatThrownBy(() -> interviewQuestion.update(content, interviewCategory, isPublic))
                    .isInstanceOf(InvalidInterviewQuestionContentException.class);
        }

        @Test
        @DisplayName("update 메서드에서 필드 값이 정상이면 InterviewQuestion 객체를 업데이트 합니다.")
        void createInterviewQuestionObjectWhenFieldValuesAreValid() {
            // given
            InterviewQuestion interviewQuestion = InterviewQuestion.of("content", InterviewCategory.PERSONAL, true);
            String content = "updated content";
            InterviewCategory interviewCategory = InterviewCategory.SOCIAL;
            boolean isPublic = false;

            // when
            interviewQuestion.update(content, interviewCategory, isPublic);

            // then
            assertThat(interviewQuestion).isNotNull();
            assertThat(interviewQuestion.getContent()).isEqualTo(content);
            assertThat(interviewQuestion.isPublic()).isEqualTo(isPublic);
        }
    }
}
