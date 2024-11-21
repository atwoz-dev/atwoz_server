package atwoz.atwoz.admin.domain.admin;

import atwoz.atwoz.common.domain.Email;
import atwoz.atwoz.common.domain.Name;
import atwoz.atwoz.common.domain.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdminTest {

    private static final Email EMAIL = Email.of("example@me.com");
    private static final Password PASSWORD = Password.of("password123^^");
    private static final Name NAME = Name.of("홍길동");
    private static final PhoneNumber PHONE_NUMBER = PhoneNumber.of("010-1234-5678");
    private static final String COMMENT = "비고";

    @Test
    @DisplayName("Admin을 생성합니다.")
    void createAdmin() {
        // when
        Admin admin = Admin.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .name(NAME)
                .phoneNumber(PHONE_NUMBER)
                .comment(COMMENT)
                .build();

        // then
        assertThat(admin).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "name", "phoneNumber"})
    @DisplayName("Admin의 값 타입이 null이면 예외를 던집니다.")
    void cannotCreateAdminWithNullValueType(String fieldName) {
        assertThatThrownBy(() ->
            Admin.builder()
                    .email(fieldName.equals("email") ? null : EMAIL)
                    .password(fieldName.equals("password") ? null : PASSWORD)
                    .name(fieldName.equals("name") ? null : NAME)
                    .phoneNumber(fieldName.equals("phoneNumber") ? null : PHONE_NUMBER)
                    .comment(COMMENT)
                    .build())
        .isInstanceOf(IllegalArgumentException.class);
    }
}