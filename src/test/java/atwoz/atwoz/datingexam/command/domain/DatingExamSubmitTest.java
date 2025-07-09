package atwoz.atwoz.datingexam.command.domain;

import atwoz.atwoz.datingexam.command.domain.exception.InvalidDatingExamSubmitAnswersException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        @DisplayName("answers가 null 또는 빈 문자열인 경우 예외가 발생한다")
        void submitWithNullOrEmptyAnswers() {
            // Given
            DatingExamSubmit datingExamSubmit = DatingExamSubmit.from(1L);
            String nullAnswers = null;
            String emptyAnswers = "";

            // When & Then
            assertThatThrownBy(() -> datingExamSubmit.submitRequiredSubjectAnswers(nullAnswers))
                .isInstanceOf(NullPointerException.class);

            assertThatThrownBy(() -> datingExamSubmit.submitRequiredSubjectAnswers(emptyAnswers))
                .isInstanceOf(InvalidDatingExamSubmitAnswersException.class);
        }

        @Test
        @DisplayName("정상적인 답변을 제출한다")
        void submitWithValidAnswers() {
            // Given
            DatingExamSubmit datingExamSubmit = DatingExamSubmit.from(1L);
            String validAnswers = "Valid Answers";

            // When
            datingExamSubmit.submitRequiredSubjectAnswers(validAnswers);

            // Then
            assertThat(datingExamSubmit.getRequiredSubjectAnswers()).isEqualTo(validAnswers);
        }
    }

    @Nested
    @DisplayName("submitPreferredSubjectAnswers 메서드 테스트")
    class SubmitPreferredSubjectAnswersTest {

        @Test
        @DisplayName("preferredSubjectId가 null이면 예외가 발생한다")
        void submitWithNullPreferredSubjectId() {
            // Given
            DatingExamSubmit datingExamSubmit = DatingExamSubmit.from(1L);
            Long nullPreferredSubjectId = null;
            String validAnswers = "Valid Answers";

            // When & Then
            assertThatThrownBy(
                () -> datingExamSubmit.submitPreferredSubjectAnswers(nullPreferredSubjectId, validAnswers))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("answers가 null 또는 빈 문자열인 경우 예외가 발생한다")
        void submitWithNullOrEmptyAnswers() {
            // Given
            DatingExamSubmit datingExamSubmit = DatingExamSubmit.from(1L);
            Long preferredSubjectId = 2L;
            String nullAnswers = null;
            String emptyAnswers = "";

            // When & Then
            assertThatThrownBy(() -> datingExamSubmit.submitPreferredSubjectAnswers(preferredSubjectId, nullAnswers))
                .isInstanceOf(NullPointerException.class);

            assertThatThrownBy(() -> datingExamSubmit.submitPreferredSubjectAnswers(preferredSubjectId, emptyAnswers))
                .isInstanceOf(InvalidDatingExamSubmitAnswersException.class);
        }

        @Test
        @DisplayName("정상적인 답변을 제출한다")
        void submitWithValidAnswers() {
            // Given
            DatingExamSubmit datingExamSubmit = DatingExamSubmit.from(1L);
            Long preferredSubjectId = 2L;
            String validAnswers = "Valid Answers";

            // When
            datingExamSubmit.submitPreferredSubjectAnswers(preferredSubjectId, validAnswers);

            // Then
            assertThat(datingExamSubmit.getPreferredSubjectId()).isEqualTo(preferredSubjectId);
            assertThat(datingExamSubmit.getPreferredSubjectAnswers()).isEqualTo(validAnswers);
        }
    }
}