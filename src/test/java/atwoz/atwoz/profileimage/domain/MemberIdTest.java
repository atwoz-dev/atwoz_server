package atwoz.atwoz.profileimage.domain;


import atwoz.atwoz.profileimage.domain.vo.MemberId;
import atwoz.atwoz.profileimage.exception.InvalidMemberIdException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MemberIdTest {

    @Test
    @DisplayName("멤버 ID의 값이 NULL인 경우, 유효하지 않습니다.")
    void isInvalidWhenMemberIdIsNull() {
        // Given
        Long id = null;

        // When & Then
        Assertions.assertThatThrownBy(() -> MemberId.from(id)).isInstanceOf(InvalidMemberIdException.class);
    }

    @Test
    @DisplayName("멤버 ID의 값이 NULL이 아닌 경우, 유요합니다.")
    void isValid() {
        // Given
        Long id = 1L;

        // When
        MemberId memberId = MemberId.from(id);

        // Then
        Assertions.assertThat(memberId.getValue()).isEqualTo(id);
    }
}
