package atwoz.atwoz.datingexam.domain;

import atwoz.atwoz.datingexam.domain.dto.DatingExamSubmitRequest;
import atwoz.atwoz.datingexam.domain.exception.InvalidDatingExamSubmitAnswersException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatingExamSubmitTest {

    @Nested
    @DisplayName("create 메서드 테스트")
    class CreateMethodTest {

        @Test
        @DisplayName("memberId가 null이면 예외가 발생한다")
        void createWithNullMemberId() {
            // Given
            Long nullMemberId = null;

            // When & Then
            assertThatThrownBy(() -> DatingExamSubmit.from(nullMemberId))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("정상적인 memberId로 DatingExamSubmit 객체를 생성한다")
        void createWithValidMemberId() {
            // Given
            Long memberId = 1L;

            // When
            DatingExamSubmit datingExamSubmit = DatingExamSubmit.from(memberId);

            // Then
            assertThat(datingExamSubmit.getMemberId()).isEqualTo(memberId);
        }
    }

    @Nested
    @DisplayName("submitRequiredSubjectAnswers 메서드 테스트")
    class SubmitRequiredSubjectAnswersTest {

        @Test
        @DisplayName("필수 과목 답변 최초로 제출 시, 답변을 encode 하여 저장한다")
        void submitFirstRequiredSubjectAnswers() {
            // Given
            Long memberId = 1L;
            DatingExamSubmit datingExamSubmit = DatingExamSubmit.from(memberId);
            DatingExamSubmitRequest request = mock(DatingExamSubmitRequest.class);
            DatingExamAnswerEncoder answerEncoder = mock(DatingExamAnswerEncoder.class);
            String encodedAnswer = "encoded";
            when(answerEncoder.encode(request)).thenReturn(encodedAnswer);

            // When
            datingExamSubmit.submitRequiredSubjectAnswers(request, answerEncoder);

            // Then
            assertThat(datingExamSubmit.getRequiredSubjectAnswers()).isEqualTo(encodedAnswer);
        }

        @Test
        @DisplayName("필수 과목 답변이 이미 제출된 경우, InvalidDatingExamSubmitAnswersException 예외가 발생한다")
        void submitRequiredSubjectAnswersAlreadySubmitted() {
            // Given
            Long memberId = 1L;
            DatingExamSubmit datingExamSubmit = DatingExamSubmit.from(memberId);
            DatingExamSubmitRequest request = mock(DatingExamSubmitRequest.class);
            DatingExamAnswerEncoder answerEncoder = mock(DatingExamAnswerEncoder.class);
            String encodedAnswer = "encoded";
            when(answerEncoder.encode(request)).thenReturn(encodedAnswer);

            datingExamSubmit.submitRequiredSubjectAnswers(request, answerEncoder);

            // When & Then
            assertThatThrownBy(() -> datingExamSubmit.submitRequiredSubjectAnswers(request, answerEncoder))
                .isInstanceOf(InvalidDatingExamSubmitAnswersException.class);
        }

        @Test
        @DisplayName("필수 과목 답변 encoded 값이 빈 문자열이면 InvalidDatingExamSubmitAnswersException 예외가 발생한다")
        void submitRequiredSubjectAnswersWithNullOrEmptyEncodedAnswer() {
            // Given
            Long memberId = 1L;
            DatingExamSubmit datingExamSubmit = DatingExamSubmit.from(memberId);
            DatingExamSubmitRequest request = mock(DatingExamSubmitRequest.class);
            DatingExamAnswerEncoder answerEncoder = mock(DatingExamAnswerEncoder.class);
            String encodedAnswer = " ";
            when(answerEncoder.encode(request)).thenReturn(encodedAnswer);

            // When & Then
            assertThatThrownBy(() -> datingExamSubmit.submitRequiredSubjectAnswers(request, answerEncoder))
                .isInstanceOf(InvalidDatingExamSubmitAnswersException.class);
        }

    }

    @Nested
    @DisplayName("submitPreferredSubjectAnswers 메서드 테스트")
    class SubmitPreferredSubjectAnswersTest {

        @Test
        @DisplayName("선호 과목 답변 최초로 제출 시, 답변을 encode 하여 저장한다")
        void submitFirstPreferredSubjectAnswers() {
            // Given
            Long memberId = 1L;
            DatingExamSubmit datingExamSubmit = DatingExamSubmit.from(memberId);
            DatingExamSubmitRequest request = mock(DatingExamSubmitRequest.class);
            DatingExamAnswerEncoder answerEncoder = mock(DatingExamAnswerEncoder.class);
            String encodedAnswer = "encoded";
            when(answerEncoder.encode(request)).thenReturn(encodedAnswer);

            // When
            datingExamSubmit.submitPreferredSubjectAnswers(request, answerEncoder);

            // Then
            assertThat(datingExamSubmit.getPreferredSubjectAnswers()).isEqualTo(encodedAnswer);
        }

        @Test
        @DisplayName("선호 과목 답변이 이미 제출된 경우, InvalidDatingExamSubmitAnswersException 예외가 발생한다")
        void submitPreferredSubjectAnswersAlreadySubmitted() {
            // Given
            Long memberId = 1L;
            DatingExamSubmit datingExamSubmit = DatingExamSubmit.from(memberId);
            DatingExamSubmitRequest request = mock(DatingExamSubmitRequest.class);
            DatingExamAnswerEncoder answerEncoder = mock(DatingExamAnswerEncoder.class);
            String encodedAnswer = "encoded";
            when(answerEncoder.encode(request)).thenReturn(encodedAnswer);

            datingExamSubmit.submitPreferredSubjectAnswers(request, answerEncoder);

            // When & Then
            assertThatThrownBy(() -> datingExamSubmit.submitPreferredSubjectAnswers(request, answerEncoder))
                .isInstanceOf(InvalidDatingExamSubmitAnswersException.class);
        }

        @Test
        @DisplayName("선호 과목 답변 encoded 값이 빈 문자열이면 InvalidDatingExamSubmitAnswersException 예외가 발생한다")
        void submitPreferredSubjectAnswersWithNullOrEmptyEncodedAnswer() {
            // Given
            Long memberId = 1L;
            DatingExamSubmit datingExamSubmit = DatingExamSubmit.from(memberId);
            DatingExamSubmitRequest request = mock(DatingExamSubmitRequest.class);
            DatingExamAnswerEncoder answerEncoder = mock(DatingExamAnswerEncoder.class);
            String encodedAnswer = " ";
            when(answerEncoder.encode(request)).thenReturn(encodedAnswer);

            // When & Then
            assertThatThrownBy(() -> datingExamSubmit.submitPreferredSubjectAnswers(request, answerEncoder))
                .isInstanceOf(InvalidDatingExamSubmitAnswersException.class);
        }
    }
}