package atwoz.atwoz.admin.domain;

import atwoz.atwoz.admin.command.domain.admin.*;
import atwoz.atwoz.admin.command.domain.admin.exception.IncorrectPasswordException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTest {

    @Mock
    private PasswordHasher passwordHasher;

    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "name", "phoneNumber"})
    @DisplayName("Admin의 값 타입이 null이면 NullPointerException이 발생합니다.")
    void createAdminWithNullValueTypeThrowsException(String fieldName) {
        // given
        Email email = Email.from("example@me.com");
        Password password = Password.fromHashed("hashed-password");
        Name name = Name.from("홍길동");
        PhoneNumber phoneNumber = PhoneNumber.from("01012345678");

        // when & then
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
    @DisplayName("비밀번호가 일치하지 않으면 PasswordMismatchException이 발생합니다.")
    void throwsPasswordMismatchExceptionWhenPasswordMismatch() {
        // given
        Admin admin = Admin.builder()
                .email(Email.from("example@me.com"))
                .password(Password.fromHashed("hashed-password"))
                .name(Name.from("홍길동"))
                .phoneNumber(PhoneNumber.from("01012345678"))
                .build();

        String rawPassword = "raw-password";
        String hashedPassword = "hashed-password";
        when(passwordHasher.matches(rawPassword, hashedPassword)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> admin.matchPassword(rawPassword, passwordHasher))
                .isInstanceOf(IncorrectPasswordException.class);
    }
}