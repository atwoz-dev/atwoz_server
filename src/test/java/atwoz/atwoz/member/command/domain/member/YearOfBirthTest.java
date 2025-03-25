package atwoz.atwoz.member.command.domain.member;

import atwoz.atwoz.member.command.domain.member.exception.InvalidYearOfBirthException;
import atwoz.atwoz.member.command.domain.member.vo.YearOfBirth;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

public class YearOfBirthTest {
    @Test
    @DisplayName("나이가 20살 미만인 경우, 예외 발생")
    void isInvalidWhenAgeIsLessThan20() {
        // Given
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userYear = currentYear - 18; // 19살.

        // When & Then
        Assertions.assertThatThrownBy(() -> YearOfBirth.from(userYear))
                .isInstanceOf(InvalidYearOfBirthException.class);
    }

    @Test
    @DisplayName("나이가 46살 초과인 경우, 예외 발생")
    void isInvalidWhenAgeIsMoreThan46() {
        // Given
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userYear = currentYear - 46; // 47살.

        // When & Then
        Assertions.assertThatThrownBy(() -> YearOfBirth.from(userYear))
                .isInstanceOf(InvalidYearOfBirthException.class);
    }

    @Test
    @DisplayName("나이가 20살 이상 46살 이하인 경우, 생성")
    void createYearOfBirthWhenAgeIsMoreThan19AndLessThan47() {
        // Given
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userYear = currentYear - 25; // 26살

        // When
        YearOfBirth yearOfBirth = YearOfBirth.from(userYear);

        // Then
        Assertions.assertThat(yearOfBirth.getValue()).isEqualTo(userYear);
    }

    @Test
    @DisplayName("입력값이 null 인 경우, null로 반환.")
    void createNullWhenAgeIsNull() {
        // Given
        Integer userYear = null;

        // When
        YearOfBirth yearOfBirth = YearOfBirth.from(userYear);

        // Then
        Assertions.assertThat(yearOfBirth).isNull();
    }
}
