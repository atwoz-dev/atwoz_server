package atwoz.atwoz.profileimage.domain;

import atwoz.atwoz.profileimage.domain.vo.ImageUrl;
import atwoz.atwoz.profileimage.domain.vo.MemberId;
import atwoz.atwoz.profileimage.exception.InvalidIsPrimaryException;
import atwoz.atwoz.profileimage.exception.InvalidOrderException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProfileImageTest {

    @Test
    @DisplayName("필드값에 NULL이 들어가지 않은 경우에는 성공.")
    void createWithMemberIdAndImageUrlAndIsPrimaryAndOrder() {
        // Given
        Long memberId = 1L;
        String imageUrl = "url";
        Boolean isPrimary = true;
        Integer order = 1;

        // When
        ProfileImage profileImage = ProfileImage.of(MemberId.from(memberId), ImageUrl.from(imageUrl), order, isPrimary);

        // Then
        Assertions.assertThat(profileImage).isNotNull();
    }

    @Test
    @DisplayName("order 값이 NULL인 경우, 유효하지 않음.")
    void isInvalidWhenOrderIsNull() {
        // Given
        Long memberId = 1L;
        String imageUrl = "url";
        Boolean isPrimary = true;
        Integer order = null;

        // When & Then
        Assertions.assertThatThrownBy(() -> ProfileImage.of(MemberId.from(memberId), ImageUrl.from(imageUrl), order, isPrimary)).isInstanceOf(InvalidOrderException.class);
    }

    @Test
    @DisplayName("order 값이 음수인 경우, 유효하지 않음")
    void isInvalidWhenOrderIsNegative() {
        // Given
        Long memberId = 1L;
        String imageUrl = "url";
        Boolean isPrimary = false;
        Integer order = -1;

        // When & Then
        Assertions.assertThatThrownBy(() -> ProfileImage.of(MemberId.from(memberId), ImageUrl.from(imageUrl), order, isPrimary)).isInstanceOf(InvalidOrderException.class);
    }

    @Test
    @DisplayName("isPrimary 값이 NULL인 경우, 유효하지 않음")
    void isInvalidWhenPrimaryIsNull() {
        // Given
        Long memberId = 1L;
        String imageUrl = "url";
        Boolean isPrimary = null;
        Integer order = 1;

        // When & Then
        Assertions.assertThatThrownBy(() -> ProfileImage.of(MemberId.from(memberId), ImageUrl.from(imageUrl), order, isPrimary)).isInstanceOf(InvalidIsPrimaryException.class);
    }
}
