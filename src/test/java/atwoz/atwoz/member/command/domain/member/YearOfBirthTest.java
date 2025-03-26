package atwoz.atwoz.member.command.domain.member;

import atwoz.atwoz.member.command.domain.member.exception.InvalidYearOfBirthException;
import atwoz.atwoz.member.command.domain.member.vo.YearOfBirth;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class YearOfBirthTest {

    @DisplayName("나이가 20살 미만인 경우, 예외 발생")
    @Test
    void throwExceptionWhenAgeIsLessThan20() {
        // Given
        int age = 19;

        // When & Then
        Assertions.assertThatThrownBy(() -> YearOfBirth.from(age))
                .isInstanceOf(InvalidYearOfBirthException.class);
    }

    @DisplayName("나이가 46살 초과인 경우, 예외 발생")
    @Test
    void throwExceptionWhenAgeIsMoreThan46() {
        // Given
        int age = 47;

        // When & Then
        Assertions.assertThatThrownBy(() -> YearOfBirth.from(age))
                .isInstanceOf(InvalidYearOfBirthException.class);
    }

    @DisplayName("나이가 null 인 경우, 정상 동작")
    @Test
    void createYearOfBirthWithNullAge() {
        // Given
        Integer age = null;

        // When
        YearOfBirth yearOfBirth = YearOfBirth.from(age);

        // Then
        Assertions.assertThat(yearOfBirth).isNotNull();
        Assertions.assertThat(yearOfBirth.getValue()).isNull();
    }

    @DisplayName("나이가 20살 이상 46살 이하인 경우, 정상 동작")
    void createYearOfBirthWhenAgeIsBetween20And46() {
        // Given
        Integer age = 30;

        // When
        YearOfBirth yearOfBirth = YearOfBirth.from(age);

        // Then
        Assertions.assertThat(yearOfBirth).isNotNull();
        Assertions.assertThat(yearOfBirth.getValue()).isEqualTo(age);
    }
}
