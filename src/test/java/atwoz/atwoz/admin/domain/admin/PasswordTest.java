package atwoz.atwoz.admin.domain.admin;

import atwoz.atwoz.admin.domain.Password;
import atwoz.atwoz.admin.domain.PasswordHasher;
import atwoz.atwoz.admin.domain.exception.InvalidPasswordException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordTest {

    @Mock
    private PasswordHasher passwordHasher;

    @Test
    @DisplayName("비밀번호가 10자이고, 문자, 숫자, 특수문자를 포함한 경우 유효합니다.")
    void isValidWhenPasswordHasTenCharactersIncludingLettersNumbersAndSpecialCharacters() {
        // given
        String validPassword = "pw345678^^";
        when(passwordHasher.hash(anyString()))
                .thenAnswer(invocation -> {
                    String raw = invocation.getArgument(0, String.class);
                    return "hashed" + raw;
                });

        // when
        Password password = Password.fromRaw(validPassword, passwordHasher);

        // then
        assertThat(password).isNotNull();
        assertThat(password.getHashedValue()).isEqualTo("hashed" + validPassword);
    }

    @Test
    @DisplayName("비밀번호가 20자이고, 문자, 숫자, 특수문자를 포함한 경우 유효합니다.")
    void isValidWhenPasswordHasTwentyCharactersIncludingLettersNumbersAndSpecialCharacters() {
        // given
        String validPassword = "password9012345678^^";
        when(passwordHasher.hash(anyString()))
                .thenAnswer(invocation -> {
                    String raw = invocation.getArgument(0, String.class);
                    return "hashed" + raw;
                });

        // when
        Password password = Password.fromRaw(validPassword, passwordHasher);

        // then
        assertThat(password).isNotNull();
        assertThat(password.getHashedValue()).isEqualTo("hashed" + validPassword);
    }

    @Test
    @DisplayName("비밀번호가 10자 미만인 경우 유효하지 않습니다.")
    void isInvalidWhenPasswordIsLessThan10Characters() {
        // given
        String invalidPassword = "pw123^^";

        // when & then
        assertThatThrownBy(() -> Password.fromRaw(invalidPassword, passwordHasher))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    @DisplayName("비밀번호가 20자를 초과하는 경우 유효하지 않습니다.")
    void isInvalidWhenPasswordIsGreaterThan20Characters() {
        // given
        String invalidPassword = "abcdefghijklmnopqrstuvwxyz1234567890^^";

        // when & then
        assertThatThrownBy(() -> Password.fromRaw(invalidPassword, passwordHasher))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    @DisplayName("비밀번호가 null인 경우 유효하지 않습니다.")
    void isInvalidWhenPasswordIsNull() {
        // given
        String invalidPassword = null;

        // when & then
        assertThatThrownBy(() -> Password.fromRaw(invalidPassword, passwordHasher))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("비밀번호에 문자가 포함되지 않은 경우 유효하지 않습니다.")
    void isInvalidWhenPasswordDoesNotContainCharacters() {
        // given
        String invalidPassword = "1234567890^^";

        // when & then
        assertThatThrownBy(() -> Password.fromRaw(invalidPassword, passwordHasher))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    @DisplayName("비밀번호에 숫자가 포함되지 않은 경우 유효하지 않습니다.")
    void isInvalidWhenPasswordDoesNotContainNumbers() {
        // given
        String invalidPassword = "password^^";

        // when & then
        assertThatThrownBy(() -> Password.fromRaw(invalidPassword, passwordHasher))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    @DisplayName("비밀번호에 특수문자가 포함되지 않은 경우 유효하지 않습니다.")
    void isInvalidWhenPasswordDoesNotContainSpecialCharacters() {
        // given
        String invalidPassword = "password1234";

        // when & then
        assertThatThrownBy(() -> Password.fromRaw(invalidPassword, passwordHasher))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    @DisplayName("비밀번호에 영어 대소문자 이외의 문자가 포함된 경우 유효하지 않습니다.")
    void isInvalidWhenPasswordContainsNonAlphabetCharacters() {
        // given
        String invalidPassword = "비밀번호abcd1234^^";

        // when & then
        assertThatThrownBy(() -> Password.fromRaw(invalidPassword, passwordHasher))
                .isInstanceOf(InvalidPasswordException.class);
    }
}
