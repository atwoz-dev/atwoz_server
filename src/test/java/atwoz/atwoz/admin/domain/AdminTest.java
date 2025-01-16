package atwoz.atwoz.admin.domain;

import atwoz.atwoz.admin.application.exception.PasswordMismatchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTest {

    private final Email email = Email.from("example@me.com");
    private final Password password = Password.fromHashed("hashed-password");
    private final Name name = Name.from("홍길동");
    private final PhoneNumber phoneNumber = PhoneNumber.from("01012345678");

    @Mock
    private PasswordHasher passwordHasher;

    @Test
    @DisplayName("유효한 값 타입들로 Admin을 생성합니다.")
    void createAdminWithValidValueTypes() {
        Admin admin = createAdmin();

        assertThat(admin).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "name", "phoneNumber"})
    @DisplayName("Admin의 값 타입이 null이면 예외를 던집니다.")
    void createAdminWithNullValueTypeThrowsException(String fieldName) {
        assertThatThrownBy(() ->
                Admin.builder()
                        .email(fieldName.equals("email") ? null : email)
                        .password(fieldName.equals("password") ? null : password)
                        .name(fieldName.equals("name") ? null : name)
                        .phoneNumber(fieldName.equals("phoneNumber") ? null : phoneNumber)
                        .build())
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 PasswordMismatchException을 던집니다.")
    void throwsPasswordMismatchExceptionWhenPasswordMismatch() {
        // given
        Admin admin = createAdmin();
        String rawPassword = "raw-password";

        when(passwordHasher.matches(rawPassword, password.getHashedValue())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> admin.matchPassword(rawPassword, passwordHasher))
                .isInstanceOf(PasswordMismatchException.class);
    }

    private Admin createAdmin() {
        return Admin.builder()
                .email(email)
                .password(password)
                .name(name)
                .phoneNumber(phoneNumber)
                .build();
    }
}