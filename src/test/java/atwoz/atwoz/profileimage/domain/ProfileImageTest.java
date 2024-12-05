package atwoz.atwoz.profileimage.domain;

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
        boolean isPrimary = true;

        // When
        ProfileImage profileImage = ProfileImage.of(memberId, imageUrl, isPrimary);

        // Then
        Assertions.assertThat(profileImage).isNotNull();
    }
}
