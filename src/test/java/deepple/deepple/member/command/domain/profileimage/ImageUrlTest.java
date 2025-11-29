package deepple.deepple.member.command.domain.profileimage;

import deepple.deepple.member.command.domain.profileImage.exception.InvalidImageUrlException;
import deepple.deepple.member.command.domain.profileImage.vo.ImageUrl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ImageUrlTest {

    @Test
    @DisplayName("Url의 값이 Null 인 경우 유효하지 않습니다.")
    void isInvalidWhenURlIsNull() {
        // Given
        String url = null;

        // When & Then
        Assertions.assertThatThrownBy(() -> ImageUrl.from(url))
            .isInstanceOf(InvalidImageUrlException.class);
    }

    @Test
    @DisplayName("Url의 값이 빈 문자열인 경우 유효하지 않습니다.")
    void isInvalidWhenURlIsEmpty() {
        // Given
        String url = "";

        // When & Then
        Assertions.assertThatThrownBy(() -> ImageUrl.from(url))
            .isInstanceOf(InvalidImageUrlException.class);
    }

    @Test
    @DisplayName("URL이 NULL 또는 빈 문자열이 아닌 경우, 유효합니다")
    void isValid() {
        // Given
        String url = "isNotNullAndEmpty";

        // When
        ImageUrl imageUrl = ImageUrl.from(url);

        // Then
        Assertions.assertThat(imageUrl.getValue()).isEqualTo(url);
    }
}
