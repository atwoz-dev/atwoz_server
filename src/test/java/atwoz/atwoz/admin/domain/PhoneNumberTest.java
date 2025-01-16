package atwoz.atwoz.admin.domain;

import atwoz.atwoz.admin.domain.exception.InvalidPhoneNumberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PhoneNumberTest {

    @ParameterizedTest
    @ValueSource(strings = {"01012345678", "01099999999"})
    @DisplayName("전화번호가 01012345678 형식에 맞는 경우 유효합니다.")
    void canCreatePhoneNumberWhenFormatIsValid(String validPhoneNumber) {
        // when
        PhoneNumber phoneNumber = PhoneNumber.from(validPhoneNumber);

        // then
        assertThat(phoneNumber).isNotNull();
        assertThat(phoneNumber.getValue()).isEqualTo(validPhoneNumber);
    }

    @ParameterizedTest
    @ValueSource(strings = {"01234567890", "010123456789", "", "010-1234-5678"})
    @DisplayName("전화번호 형식이 유효하지 않은 경우 InvalidPhoneNumberException이 발생합니다.")
    void isInvalidWhenFormatIsIncorrect(String invalidPhoneNumber) {
        // when & then
        assertThatThrownBy(() -> PhoneNumber.from(invalidPhoneNumber))
                .isInstanceOf(InvalidPhoneNumberException.class);
    }
}