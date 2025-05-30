package atwoz.atwoz.community.command.domain.profileexchange;

import atwoz.atwoz.community.command.domain.profileexchange.exception.SelfProfileExchangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileExchangeTest {

    @Test
    @DisplayName("자기 자신에게 프로필 교환 요청을 한 경우, 예외 발생")
    void throwExceptionWhenRequesterIdIsEqualToResponderId() {
        // Given
        long requestId = 1L;

        // When & Then
        assertThatThrownBy(() -> ProfileExchange.request(requestId, requestId))
            .isInstanceOf(SelfProfileExchangeException.class);
    }

    @Test
    @DisplayName("프로필 교환을 신청할 경우, 신청 대기 상태로 생성한다.")
    void createProfileExchangeWithWaitingStatus() {
        // Given
        long requesterId = 1L;
        long responderId = 3L;

        // When
        ProfileExchange profileExchange = ProfileExchange.request(requesterId, responderId);

        // Then
        assertThat(profileExchange.getRequesterId()).isEqualTo(requesterId);
        assertThat(profileExchange.getResponderId()).isEqualTo(responderId);
        assertThat(profileExchange.getStatus()).isEqualTo(ProfileExchangeStatus.WAITING);
    }
}
