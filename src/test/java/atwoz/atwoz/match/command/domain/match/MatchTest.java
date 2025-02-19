package atwoz.atwoz.match.command.domain.match;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MatchTest {

    @Test
    @DisplayName("요청자 아이디가 null인 경우 예외 반환")
    void throwsExceptionWhenRequesterIdIsNull() {
        // Given
        Long requesterId = null;
        Long responderId = 2L;
        String requestMessage = "매칭을 요청합니다!";

        // When & Then
        Assertions.assertThatThrownBy(() -> Match.requestMatching(requesterId, responderId, requestMessage))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("응답자 아이디가 null인 경우 예외 반환")
    void throwsExceptionWhenResponderIdIsNull() {
        // Given
        Long requesterId = 1L;
        Long responderId = null;
        String requestMessage = "매칭을 요청합니다!";

        // When & Then
        Assertions.assertThatThrownBy(() -> Match.requestMatching(requesterId, responderId, requestMessage))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("요청 메세지가 null인 경우 예외 반환")
    void throwsExceptionWhenRequestMessageIsNull() {
        // Given
        Long requesterId = 1L;
        Long responderId = 2L;
        String requestMessage = null;

        // When & Then
        Assertions.assertThatThrownBy(() -> Match.requestMatching(requesterId, responderId, requestMessage))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("모든 값이 null이 아닌 경우 성공.")
    void createMatch() {
        // Given
        Long requesterId = 1L;
        Long responderId = 2L;
        String requestMessage = "매칭을 요청합니다.";

        // When
        Match match = Match.requestMatching(requesterId, responderId, requestMessage);

        // Then
        Assertions.assertThat(match.getRequesterId()).isEqualTo(requesterId);
        Assertions.assertThat(match.getResponderId()).isEqualTo(responderId);
        Assertions.assertThat(match.getRequestMessage()).isEqualTo(requestMessage);
    }
}
