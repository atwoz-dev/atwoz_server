package atwoz.atwoz.admin.domain;

import atwoz.atwoz.admin.command.domain.admin.Name;
import atwoz.atwoz.admin.command.domain.admin.exception.InvalidNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NameTest {

    @ParameterizedTest
    @ValueSource(strings = {"홍길동", "John", "김", "1234567890"})
    @DisplayName("10자 이하의 문자나 숫자로 Name을 생성할 수 있습니다.")
    void canCreateNameWhenFormatIsValid(String validName) {
        // when
        Name name = Name.from(validName);

        // then
        assertThat(name).isNotNull();
        assertThat(name.getValue()).isEqualTo(validName);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "12345678910", "홍길동123^^", "John Doe"})
    @DisplayName("잘못된 형식의 이름은 InvalidNameException이 발생합니다.")
    void throwInvalidNameExceptionWhenFormatIsInvalid(String invalidName) {
        // when & then
        assertThatThrownBy(() -> Name.from(invalidName))
                .isInstanceOf(InvalidNameException.class);
    }
}