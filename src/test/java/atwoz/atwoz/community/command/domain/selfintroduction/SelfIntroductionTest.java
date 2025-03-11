package atwoz.atwoz.community.command.domain.selfintroduction;

import atwoz.atwoz.community.command.domain.selfintroduction.exception.InvalidSelfIntroductionContentException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SelfIntroductionTest {

    @Nested
    @DisplayName("셀프 소개 생성 실패 테스트")
    class Fail {
        @Test
        @DisplayName("멤버 아이디가 null인 경우, 예외 발생")
        void throwExceptionWhenMemberIdIsNull() {
            // Given
            Long memberId = null;
            String content = "셀프 소개 내용.";

            // When & Then
            Assertions.assertThatThrownBy(() -> SelfIntroduction.write(memberId, content))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("셀프 소개의 내용이 null인 경우, 예외 발생")
        void throwExceptionWhenContentIsNull() {
            // Given
            Long memberId = 1L;
            String content = null;

            // When & Then
            Assertions.assertThatThrownBy(() -> SelfIntroduction.write(memberId, content))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("셀프 소개의 내용이 30자 미만인 경우, 예외 발생")
        void throwExceptionWhenContentIsLessThen30() {
            // Given
            Long memberId = 1L;
            String content = "30자 이하.";

            // When & Then
            Assertions.assertThatThrownBy(() -> SelfIntroduction.write(memberId, content))
                    .isInstanceOf(InvalidSelfIntroductionContentException.class);
        }
    }

    @Test
    @DisplayName("멤버 아이디와 셀프 소개의 내용이 30자 이상인 경우, 정상 동작.")
    void writeSelfIntroduction() {
        // Given
        Long memberId = 1L;
        String content = "셀프 소개 내용이 공백 포함하여 최소 30자 이상이어야 합니다.";

        // When
        SelfIntroduction selfIntroduction = SelfIntroduction.write(memberId, content);

        // Then
        Assertions.assertThat(selfIntroduction).isNotNull();
        Assertions.assertThat(selfIntroduction.getMemberId()).isEqualTo(memberId);
        Assertions.assertThat(selfIntroduction.getContent()).isEqualTo(content);
    }
}
