package atwoz.atwoz.admin.domain.admin;

import atwoz.atwoz.admin.domain.Email;
import atwoz.atwoz.admin.domain.exception.InvalidEmailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    @DisplayName("이메일 주소가 형식에 맞는 경우 유효합니다.")
    void isValidWhenFormatIsValid() {
        // given
        String validEmail = "example@me.com";

        // when
        Email email = Email.from(validEmail);

        // then
        assertThat(email).isNotNull();
        assertThat(email.getAddress()).isEqualTo(validEmail);
    }

    @Test
    @DisplayName("이메일 주소에 로컬이 없는 경우 유효하지 않습니다.")
    void isInValidWhenLocalPartIsAbsent() {
        // given
        String invalidEmail = "@me.com";

        // when & then
        assertThatThrownBy(() -> Email.from(invalidEmail))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    @DisplayName("이메일 주소에 @가 없는 경우 유효하지 않습니다.")
    void isInValidWhenAtIsAbsent() {
        // given
        String invalidEmail = "exampleme.com";

        // when & then
        assertThatThrownBy(() -> Email.from(invalidEmail))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    @DisplayName("이메일 주소에 도메인이 없는 경우 유효하지 않습니다.")
    void isInValidWhenDomainIsAbsent() {
        // given
        String invalidEmail = "example@";

        // when & then
        assertThatThrownBy(() -> Email.from(invalidEmail))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    @DisplayName("이메일 주소의 도메인이 유효하지 않은 경우 유효하지 않습니다.")
    void isInValidWhenDomainIsInvalid() {
        // given
        String invalidEmail = "example@me";

        // when & then
        assertThatThrownBy(() -> Email.from(invalidEmail))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    @DisplayName("이메일 주소에 허용되지 않은 문자가 포함된 경우 유효하지 않습니다.")
    void isInValidWhenEmailContainsForbiddenCharacters() {
        // given
        String invalidEmail = "홍길동@me.com";

        // when & then
        assertThatThrownBy(() -> Email.from(invalidEmail))
                .isInstanceOf(InvalidEmailException.class);
    }
}