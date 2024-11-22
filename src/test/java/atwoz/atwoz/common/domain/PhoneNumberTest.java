package atwoz.atwoz.common.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PhoneNumberTest {

    @Test
    @DisplayName("전화번호가 형식에 맞는 경우 유효합니다.")
    void isValidWhenFormatIsValid() {
        // given
        String validPhoneNumber = "010-1234-5678";

        // when
        PhoneNumber phoneNumber = PhoneNumber.of(validPhoneNumber);

        // then
        assertThat(phoneNumber).isNotNull();
        assertThat(phoneNumber.getValue()).isEqualTo(validPhoneNumber);
    }

    @Test
    @DisplayName("전화번호가 0으로 시작하지 않는 경우 유효하지 않습니다.")
    void isInvalidWhenPhoneNumberDoesNotStartWithZero() {
        // given
        String invalidPhoneNumber = "110-1234-5678";

        // when & then
        assertThatThrownBy(() -> PhoneNumber.of(invalidPhoneNumber))
                .isInstanceOf(InvalidPhoneNumberException.class);
    }

    @Test
    @DisplayName("전화번호 가운데가 네 자리보다 큰 경우 유효하지 않습니다.")
    void isInvalidWhenMiddlePartOfPhoneNumberExceedsFourDigits() {
        // given
        String invalidPhoneNumber = "010-123456-7890";

        // when & then
        assertThatThrownBy(() -> PhoneNumber.of(invalidPhoneNumber))
                .isInstanceOf(InvalidPhoneNumberException.class);
    }

    @Test
    @DisplayName("전화번호 마지막이 네 자리보다 큰 경우 유효하지 않습니다.")
    void isInvalidWhenLastPartOfPhoneNumberExceedsFourDigits() {
        // given
        String invalidPhoneNumber = "010-1234-567890";

        // when & then
        assertThatThrownBy(() -> PhoneNumber.of(invalidPhoneNumber))
                .isInstanceOf(InvalidPhoneNumberException.class);
    }

    @Test
    @DisplayName("전화번호가 null인 경우 유효하지 않습니다.")
    void isInvalidWhenPhoneNumberIsNull() {
        // given
        String invalidPhoneNumber = null;

        // when & then
        assertThatThrownBy(() -> PhoneNumber.of(invalidPhoneNumber))
                .isInstanceOf(InvalidPhoneNumberException.class);
    }

    @Test
    @DisplayName("전화번호에 허용되지 않는 문자가 포함된 경우 유효하지 않습니다.")
    void isInvalidWhenPhoneNumberContainsInvalidCharacters() {
        // given
        String invalidPhoneNumber = "010-1234-aaaa";

        // when & then
        assertThatThrownBy(() -> PhoneNumber.of(invalidPhoneNumber))
                .isInstanceOf(InvalidPhoneNumberException.class);
    }
}