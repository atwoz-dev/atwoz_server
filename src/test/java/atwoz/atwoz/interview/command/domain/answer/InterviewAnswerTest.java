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
    class ofMethodTest {

        @ParameterizedTest
        @ValueSource(strings = {"content is null", "questionId is null", "memberId is null"})
        @DisplayName("of 메서드에서 필드 값이 null이면 예외를 던집니다.")
        void throwsExceptionWhenFieldValueIsNull(String fieldName) {
            // given
            Long questionId = fieldName.equals("questionId is null") ? null : 1L;
            Long memberId = fieldName.equals("memberId is null") ? null : 2L;
            String content = fieldName.equals("content is null") ? null : "content";

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
            String content = "content";

            // when
            InterviewAnswer interviewAnswer = InterviewAnswer.of(questionId, memberId, content);

            // then
            assertThat(interviewAnswer).isNotNull();
        }
    }

    @Nested
    @DisplayName("submitFirstInterviewAnswer 메서드 테스트")
    class submitFirstInterviewAnswerMethodTest {

        @Test
        @DisplayName("첫 번째 면접 답변을 제출하면 FirstInterviewSubmittedEvent를 발생시킨다.")
        void raiseFirstInterviewSubmittedEventWhenSubmitFirstInterviewAnswer() {
            // given
            Long questionId = 1L;
            Long memberId = 2L;
            InterviewAnswer interviewAnswer = InterviewAnswer.of(questionId, memberId, "content");

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
}