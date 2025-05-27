package atwoz.atwoz.community.command.domain.profileexchange;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileExchangeTest {
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
