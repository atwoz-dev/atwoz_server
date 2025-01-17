package atwoz.atwoz.member.domain;

import atwoz.atwoz.member.domain.member.vo.Nickname;
import atwoz.atwoz.member.exception.InvalidNickNameException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NickNameTest {

    @Test
    @DisplayName("닉네임이 입력되지 않은 경우 유효하지 않습니다.")
    void isInValidWhenNickNameIsNull() {
        // Given
        String nickname = null;

        // When & Then
        Assertions.assertThatThrownBy(() -> Nickname.from(nickname))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("닉네임이 공백으로 입력된 경우 유효하지 않습니다.")
    void isInValidWhenNickNameIsEmpty() {
        // Given
        String nickname = "";

        // When & Then
        Assertions.assertThatThrownBy(() -> Nickname.from(nickname))
                .isInstanceOf(InvalidNickNameException.class);
    }

    @Test
    @DisplayName("닉네임에 특수문자가 포함된 경우 유효하지 않습니다.")
    void isInvalidWhenNickNameIncludesSpecialCharacters() {
        // Given
        String nickname = "kong@@tae";

        // When & Then
        Assertions.assertThatThrownBy(() -> Nickname.from(nickname))
                .isInstanceOf(InvalidNickNameException.class);
    }

    @Test
    @DisplayName("닉네임의 길이가 10자를 초과할 경우 유효하지 않습니다.")
    void isInvalidWhenNicknameExceedsTenLength() {
        // Given
        String nickname = "kongtaehyeon1";

        // When & Then
        Assertions.assertThatThrownBy(() -> Nickname.from(nickname))
                .isInstanceOf(InvalidNickNameException.class);
    }

    @Test
    @DisplayName("닉네임의 길이가 10자 이내이며, 특수문자를 포함하지 않은 경우 유효합니다.")
    void isValid() {
        // Given
        String validNickname = "kong1tae";

        // When
        Nickname nickname = Nickname.from(validNickname);

        // When & Then
        Assertions.assertThat(nickname).isNotNull();
        Assertions.assertThat(nickname.getValue()).isEqualTo(validNickname);
    }
}
