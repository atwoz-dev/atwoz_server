package atwoz.atwoz.common.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PhoneNumberTest {

    @Test
    @DisplayName("전화번호가 형식에 맞는 경우 유효합니다.")
    void isValidWhenFormatIsCorrect() {
        // given
        String validPhoneNumber = "01012345678";

        // when
        PhoneNumber phoneNumber = PhoneNumber.from(validPhoneNumber);

        // then
        assertThat(phoneNumber).isNotNull();
        assertThat(phoneNumber.getValue()).isEqualTo(validPhoneNumber);
    }

    @Test
    @DisplayName("전화번호가 010으로 시작하지 않는 경우 유효하지 않습니다.")
    void isInvalidWhenPhoneNumberDoesNotStartWith010() {
        // given
        String invalidPhoneNumber = "01234567890";

        // when & then
        assertThatThrownBy(() -> PhoneNumber.from(invalidPhoneNumber))
                .isInstanceOf(InvalidPhoneNumberException.class);
    }

    @Test
    @DisplayName("전화번호가 11자리보다 큰 경우 유효하지 않습니다.")
    void isInvalidWhenPhoneNumberExceedsElevenDigits() {
        // given
        String invalidPhoneNumber = "010123456789";

        // when & then
        assertThatThrownBy(() -> PhoneNumber.from(invalidPhoneNumber))
                .isInstanceOf(InvalidPhoneNumberException.class);
    }

    @Test
    @DisplayName("전화번호가 null인 경우 유효하지 않습니다.")
    void isInvalidWhenPhoneNumberIsNull() {
        // given
        String invalidPhoneNumber = null;

        // when & then
        assertThatThrownBy(() -> PhoneNumber.from(invalidPhoneNumber))
                .isInstanceOf(InvalidPhoneNumberException.class);
    }

    @Test
    @DisplayName("전화번호가 빈 문자열인 경우 유효하지 않습니다.")
    void isInvalidWhenPhoneNumberIsEmpty() {
        // given
        String invalidPhoneNumber = "";

        // when & then
        assertThatThrownBy(() -> PhoneNumber.from(invalidPhoneNumber))
                .isInstanceOf(InvalidPhoneNumberException.class);
    }

    @Test
    @DisplayName("전화번호에 숫자가 아닌 값이 포함된 경우 유효하지 않습니다.")
    void isInvalidWhenPhoneNumberContainsNonNumericCharacters() {
        // given
        String invalidPhoneNumber = "010-1234-5678";

        // when & then
        assertThatThrownBy(() -> PhoneNumber.from(invalidPhoneNumber))
                .isInstanceOf(InvalidPhoneNumberException.class);
    }
}