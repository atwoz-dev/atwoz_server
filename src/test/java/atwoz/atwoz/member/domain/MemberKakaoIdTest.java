package atwoz.atwoz.member.domain;

import atwoz.atwoz.member.domain.member.vo.KakaoId;
import atwoz.atwoz.member.exception.InvalidKakaoIdException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MemberKakaoIdTest {

    @Test
    @DisplayName("길이가 3글자 미만인 경우, 유효하지 않습니다.")
    void isInvalidWhenIdLengthIsLessThan3() {
        // Given
        String kakaoId = "ab";

        // When & Then
        Assertions.assertThatThrownBy(() -> KakaoId.from(kakaoId)).isInstanceOf(InvalidKakaoIdException.class);
    }

    @Test
    @DisplayName("패턴과 일치하지 않은 경우, 유효하지 않습니다.")
    void isInvalidWhenIdIsNotMatchedByPattern() {
        // Given
        String kakaoId = "abcd&*";

        // When & Then
        Assertions.assertThatThrownBy(() -> KakaoId.from(kakaoId)).isInstanceOf(InvalidKakaoIdException.class);
    }
}
