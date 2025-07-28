package atwoz.atwoz.datingexam.domain;

import atwoz.atwoz.datingexam.domain.exception.InvalidSubjectNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatingExamSubjectTest {

    @Test
    @DisplayName("name이 null 또는 빈 문자열인 경우 예외가 발생한다.")
    void createWithNullOrEmptyName() {
        // Given
        String nullName = null;
        String emptyName = "";
        SubjectType subjectType = SubjectType.REQUIRED;

        // When & Then
        assertThatThrownBy(() -> DatingExamSubject.create(nullName, subjectType))
            .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> DatingExamSubject.create(emptyName, subjectType))
            .isInstanceOf(InvalidSubjectNameException.class);
    }

    @Test
    @DisplayName("type이 null인 경우 예외가 발생한다.")
    void createWithNullType() {
        // Given
        String subjectName = "Test Subject";
        SubjectType nullType = null;

        // When & Then
        assertThatThrownBy(() -> DatingExamSubject.create(subjectName, nullType))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("정상적인 subject 생성")
    void createWithValidParameters() {
        // Given
        String subjectName = "Valid Subject";
        SubjectType subjectType = SubjectType.REQUIRED;

        // When
        DatingExamSubject subject = DatingExamSubject.create(subjectName, subjectType);

        // Then
        assertThat(subject.getName()).isEqualTo(subjectName);
        assertThat(subject.getType()).isEqualTo(subjectType);
        assertThat(subject.isPublic()).isTrue();
    }
}