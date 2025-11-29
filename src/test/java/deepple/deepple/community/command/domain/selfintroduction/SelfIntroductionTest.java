package deepple.deepple.community.command.domain.selfintroduction;

import deepple.deepple.community.command.domain.selfintroduction.exception.InvalidSelfIntroductionContentException;
import deepple.deepple.community.command.domain.selfintroduction.exception.InvalidSelfIntroductionTitleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SelfIntroductionTest {

    @Test
    @DisplayName("멤버 아이디와 제목이 존재하고, 셀프 소개의 내용이 30자 이상인 경우, 정상 동작.")
    void writeSelfIntroduction() {
        // Given
        Long memberId = 1L;
        String title = "셀프 소개 제목";
        String content = "셀프 소개 내용이 공백 포함하여 최소 30자 이상이어야 합니다.";

        // When
        SelfIntroduction selfIntroduction = SelfIntroduction.write(memberId, title, content);

        // Then
        assertThat(selfIntroduction).isNotNull();
        assertThat(selfIntroduction.getMemberId()).isEqualTo(memberId);
        assertThat(selfIntroduction.getContent()).isEqualTo(content);
    }

    @Nested
    @DisplayName("셀프 소개 생성 실패 테스트")
    class Fail {
        @Test
        @DisplayName("멤버 아이디가 null인 경우, 예외 발생")
        void throwExceptionWhenMemberIdIsNull() {
            // Given
            Long memberId = null;
            String title = "셀프 소개 제목";
            String content = "셀프 소개 내용.";

            // When & Then
            assertThatThrownBy(() -> SelfIntroduction.write(memberId, title, content))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("셀프 소개의 제목이 null인 경우, 예외 발생")
        void throwsExceptionWhenTitleIsNull() {
            // Given
            Long memberId = 1L;
            String title = null;
            String content = "셀프 소개 내용.";

            // When & Then
            assertThatThrownBy(() -> SelfIntroduction.write(memberId, title, content))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("셀프 소개의 제목이 빈 문자열이면, 예외 발생")
        void throwsExceptionWhenTitleIsBlank() {
            // Given
            Long memberId = 1L;
            String title = " ";
            String content = "셀프 소개 내용.";

            // When & Then
            assertThatThrownBy(() -> SelfIntroduction.write(memberId, title, content))
                .isInstanceOf(InvalidSelfIntroductionTitleException.class);
        }

        @Test
        @DisplayName("셀프 소개의 내용이 null인 경우, 예외 발생")
        void throwExceptionWhenContentIsNull() {
            // Given
            Long memberId = 1L;
            String title = "셀프 소개 제목";
            String content = null;

            // When & Then
            assertThatThrownBy(() -> SelfIntroduction.write(memberId, title, content))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("셀프 소개의 내용이 30자 미만인 경우, 예외 발생")
        void throwExceptionWhenContentIsLessThen30() {
            // Given
            Long memberId = 1L;
            String title = "셀프 소개 제목";
            String content = "30자 이하.";

            // When & Then
            assertThatThrownBy(() -> SelfIntroduction.write(memberId, title, content))
                .isInstanceOf(InvalidSelfIntroductionContentException.class);
        }
    }

    @DisplayName("셀프 소개의 공개 여부 변경 테스트")
    @Nested
    class Update {
        @Test
        @DisplayName("셀프 소개를 비공개로 변경한다.")
        void updateClose() {
            // Given
            Long memberId = 1L;
            String title = "셀프 소개 제목";
            String content = "셀프 소개 내용이 공백 포함하여 최소 30자 이상이어야 합니다.";
            SelfIntroduction selfIntroduction = SelfIntroduction.write(memberId, title, content);

            // When
            selfIntroduction.close();

            // Then
            assertThat(selfIntroduction.isOpened()).isFalse();
        }

        @Test
        @DisplayName("셀프 소개를 공개로 변경한다.")
        void updateOpen() {
            // Given
            Long memberId = 1L;
            String title = "셀프 소개 제목";
            String content = "셀프 소개 내용이 공백 포함하여 최소 30자 이상이어야 합니다.";
            SelfIntroduction selfIntroduction = SelfIntroduction.write(memberId, title, content);

            // When
            selfIntroduction.open();

            // Then
            assertThat(selfIntroduction.isOpened()).isTrue();
        }
    }
}
