package atwoz.atwoz.community.command.domain.introduction;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class IntroductionTest {

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
            Assertions.assertThatThrownBy(() -> Introduction.write(memberId, content))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("셀프 소개의 내용이 null인 경우, 예외 발생")
        void throwExceptionWhenContentIsNull() {
            // Given
            Long memberId = 1L;
            String content = null;

            // When & Then
            Assertions.assertThatThrownBy(() -> Introduction.write(memberId, content))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    @DisplayName("멤버 아이디와 셀프 소개의 내용이 null이 아닌 경우, 정상 동작.")
    void writeSelfIntroduction() {
        // Given
        Long memberId = 1L;
        String content = "셀프 소개 내용.";

        // When
        Introduction introduction = Introduction.write(memberId, content);

        // Then
        Assertions.assertThat(introduction).isNotNull();
        Assertions.assertThat(introduction.getMemberId()).isEqualTo(memberId);
        Assertions.assertThat(introduction.getContent()).isEqualTo(content);
    }
}
