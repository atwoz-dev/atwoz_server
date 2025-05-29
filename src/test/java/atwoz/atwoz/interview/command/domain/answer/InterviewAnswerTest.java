package atwoz.atwoz.interview.command.domain.answer;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.interview.command.domain.answer.event.FirstInterviewSubmittedEvent;
import atwoz.atwoz.interview.command.domain.answer.exception.InvalidInterviewAnswerContentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

class InterviewAnswerTest {

    @Nested
    @DisplayName("of 메서드 테스트")
    class OfMethodTest {

        @ParameterizedTest
        @ValueSource(strings = {"body is null", "questionId is null", "memberId is null"})
        @DisplayName("of 메서드에서 필드 값이 null이면 예외를 던집니다.")
        void throwsExceptionWhenFieldValueIsNull(String fieldName) {
            // given
            Long questionId = fieldName.equals("questionId is null") ? null : 1L;
            Long memberId = fieldName.equals("memberId is null") ? null : 2L;
            String content = fieldName.equals("body is null") ? null : "body";

            // when & then
            assertThatThrownBy(() -> InterviewAnswer.of(questionId, memberId, content))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("of 메서드에서 content가 blank이면 예외를 던집니다.")
        void throwsExceptionWhenContentIsBlank() {
            // given
            Long questionId = 1L;
            Long memberId = 2L;
            String content = " ";

            // when & then
            assertThatThrownBy(() -> InterviewAnswer.of(questionId, memberId, content))
                .isInstanceOf(InvalidInterviewAnswerContentException.class);
        }

        @Test
        @DisplayName("of 메서드에서 필드 값이 정상이면 InterviewAnswer 객체를 생성합니다.")
        void createInterviewAnswerObjectWhenFieldValuesAreValid() {
            // given
            Long questionId = 1L;
            Long memberId = 2L;
            String content = "body";

            // when
            InterviewAnswer interviewAnswer = InterviewAnswer.of(questionId, memberId, content);

            // then
            assertThat(interviewAnswer).isNotNull();
        }
    }

    @Nested
    @DisplayName("submitFirstInterviewAnswer 메서드 테스트")
    class SubmitFirstInterviewAnswerMethodTest {

        @Test
        @DisplayName("첫 번째 면접 답변을 제출하면 FirstInterviewSubmittedEvent를 발생시킨다.")
        void raiseFirstInterviewSubmittedEventWhenSubmitFirstInterviewAnswer() {
            // given
            Long questionId = 1L;
            Long memberId = 2L;
            InterviewAnswer interviewAnswer = InterviewAnswer.of(questionId, memberId, "body");

            try (MockedStatic<Events> eventsMockedStatic = mockStatic(Events.class)) {
                // When
                interviewAnswer.submitFirstInterviewAnswer();

                // Then
                eventsMockedStatic.verify(() ->
                    Events.raise(argThat((FirstInterviewSubmittedEvent event) ->
                        event.getMemberId().equals(memberId)
                    )), times(1));
            }
        }
    }

    @Nested
    @DisplayName("updateContent 메서드 테스트")
    class UpdateContentMethodTest {

        @Test
        @DisplayName("updateContent 메서드에서 content가 null이면 예외를 던집니다.")
        void throwsExceptionWhenContentIsNull() {
            // given
            Long questionId = 1L;
            Long memberId = 2L;
            InterviewAnswer interviewAnswer = InterviewAnswer.of(questionId, memberId, "content");

            // when & then
            assertThatThrownBy(() -> interviewAnswer.updateContent(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("updateContent 메서드에서 content가 blank이면 예외를 던집니다.")
        void throwsExceptionWhenContentIsBlank() {
            // given
            Long questionId = 1L;
            Long memberId = 2L;
            InterviewAnswer interviewAnswer = InterviewAnswer.of(questionId, memberId, "content");

            // when & then
            assertThatThrownBy(() -> interviewAnswer.updateContent(" "))
                .isInstanceOf(InvalidInterviewAnswerContentException.class);
        }

        @Test
        @DisplayName("updateContent 메서드에서 content가 정상이면 content를 업데이트합니다.")
        void updateContentWhenContentIsValid() {
            // given
            Long questionId = 1L;
            Long memberId = 2L;
            String prevContent = "content";
            InterviewAnswer interviewAnswer = InterviewAnswer.of(questionId, memberId, prevContent);

            // when
            String updatedContent = "updated content";
            interviewAnswer.updateContent(updatedContent);

            // then
            assertThat(interviewAnswer.getContent()).isEqualTo(updatedContent);
        }
    }

    @Nested
    @DisplayName("isAnsweredBy 메서드 테스트")
    class IsAnsweredByMethodTest {

        @Test
        @DisplayName("isAnsweredBy 메서드에서 memberId가 null이면 예외를 던집니다.")
        void throwsExceptionWhenMemberIdIsNull() {
            // given
            Long questionId = 1L;
            Long memberId = 2L;
            InterviewAnswer interviewAnswer = InterviewAnswer.of(questionId, memberId, "content");

            // when & then
            Long nullMemberId = null;
            assertThatThrownBy(() -> interviewAnswer.isAnsweredBy(nullMemberId))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("isAnsweredBy 메서드에서 memberId가 interviewAnswer.memberId와 일치하면 true를 반환합니다.")
        void returnTrueWhenMemberIdIsValid() {
            // given
            Long questionId = 1L;
            Long memberId = 2L;
            InterviewAnswer interviewAnswer = InterviewAnswer.of(questionId, memberId, "content");

            // when
            boolean result = interviewAnswer.isAnsweredBy(memberId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("isAnsweredBy 메서드에서 memberId가 interviewAnswer.memberId와 일치하지 않으면 false를 반환합니다.")
        void returnFalseWhenMemberIdIsInvalid() {
            // given
            Long questionId = 1L;
            Long memberId = 2L;
            InterviewAnswer interviewAnswer = InterviewAnswer.of(questionId, memberId, "content");

            // when
            boolean result = interviewAnswer.isAnsweredBy(3L);

            // then
            assertThat(result).isFalse();
        }
    }
}