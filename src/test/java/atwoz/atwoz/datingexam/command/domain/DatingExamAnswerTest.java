package atwoz.atwoz.datingexam.command.domain;

import atwoz.atwoz.datingexam.command.domain.exception.InvalidDatingExamAnswerContentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatingExamAnswerTest {

    @Test
    @DisplayName("content이 null 또는 빈 문자열인 경우 예외가 발생한다.")
    void createWithNullOrEmptyContent() {
        // Given
        Long questionId = 1L;
        String nullContent = null;
        String emptyContent = "";

        // When & Then
        assertThatThrownBy(() -> DatingExamAnswer.create(questionId, nullContent))
            .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> DatingExamAnswer.create(questionId, emptyContent))
            .isInstanceOf(InvalidDatingExamAnswerContentException.class);
    }

    @Test
    @DisplayName("questionId이 null인 경우 예외가 발생한다.")
    void createWithNullQuestionId() {
        // Given
        String content = "Valid Answer Content";
        Long nullQuestionId = null;

        // When & Then
        assertThatThrownBy(() -> DatingExamAnswer.create(nullQuestionId, content))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("정상적인 answer 생성")
    void createWithValidParameters() {
        // Given
        Long questionId = 1L;
        String content = "Valid Answer Content";

        // When
        DatingExamAnswer answer = DatingExamAnswer.create(questionId, content);

        // Then
        assertThat(answer.getQuestionId()).isEqualTo(questionId);
        assertThat(answer.getContent()).isEqualTo(content);
    }
}