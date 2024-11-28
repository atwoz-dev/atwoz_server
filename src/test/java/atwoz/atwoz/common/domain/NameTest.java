package atwoz.atwoz.common.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NameTest {

    @Test
    @DisplayName("이름이 한 글자이고 문자(한글, 영어)나 숫자인 경우 유효합니다.")
    void isValidWhenNameIsOneCharacterOrNumber() {
        // given
        String validName = "김";

        // when
        Name name = Name.from(validName);

        // then
        assertThat(name).isNotNull();
        assertThat(name.getValue()).isEqualTo(validName);
    }

    @Test
    @DisplayName("이름이 열 글자이고 문자(한글, 영어)나 숫자인 경우 유효합니다.")
    void isValidWhenNameIsTenCharacterOrNumber() {
        // given
        String validName = "1234567890";

        // when
        Name name = Name.from(validName);

        // then
        assertThat(name).isNotNull();
        assertThat(name.getValue()).isEqualTo(validName);
    }

    @Test
    @DisplayName("이름이 공백인 경우 유효하지 않습니다.")
    void isInValidWhenNameIsEmpty() {
        // given
        String invalidName = "";

        // when & then
        assertThatThrownBy(() -> Name.from(invalidName))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    @DisplayName("이름이 null인 경우 유효하지 않습니다.")
    void isInValidWhenNameIsNull() {
        // given
        String invalidName = null;

        // when & then
        assertThatThrownBy(() -> Name.from(invalidName))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    @DisplayName("이름이 열 글자를 초과하는 경우 유효하지 않습니다.")
    void isInValidWhenNameIsGreaterThanTenCharacters() {
        // given
        String invalidName = "12345678910";

        // when & then
        assertThatThrownBy(() -> Name.from(invalidName))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    @DisplayName("이름에 허용되지 않는 문자가 포함된 경우 유효하지 않습니다.")
    void isInValidWhenNameContainsInvalidCharacters() {
        // given
        String invalidName = "홍길동123^^";

        // when & then
        assertThatThrownBy(() -> Name.from(invalidName))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    @DisplayName("이름에 공백이 포함된 경우 유효하지 않습니다.")
    void isInvalidWhenNameContainsWhitespace() {
        // given
        String invalidName = "John Doe";

        // when & then
        assertThatThrownBy(() -> Name.from(invalidName))
                .isInstanceOf(InvalidNameException.class);
    }
}