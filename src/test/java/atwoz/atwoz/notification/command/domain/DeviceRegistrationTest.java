package atwoz.atwoz.notification.command.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceRegistrationTest {

    @Test
    @DisplayName("of(): registrationToken 초기화 및 기본 활성화 상태")
    void ofInitializesFields() {
        // given
        long userId = 1L;
        String deviceId = "device-123";
        String token = "reg-token-xyz";

        // when
        var reg = DeviceRegistration.of(userId, deviceId, token);

        // then
        assertThat(reg).isNotNull();
        assertThat(reg.getRegistrationToken()).isEqualTo(token);
        assertThat(reg.isActive()).isTrue();
    }

    @Test
    @DisplayName("deactivate(): 활성화 상태를 false로 변경")
    void deactivateSetsInactive() {
        // given
        var reg = DeviceRegistration.of(1L, "device-123", "token");

        // when
        reg.deactivate();

        // then
        assertThat(reg.isActive()).isFalse();
    }

    @Test
    @DisplayName("activate(): 비활성화된 상태를 true로 복원")
    void activateRestoresActive() {
        // given
        var reg = DeviceRegistration.of(1L, "device-123", "token");
        reg.deactivate();
        assertThat(reg.isActive()).isFalse();

        // when
        reg.activate();

        // then
        assertThat(reg.isActive()).isTrue();
    }
}
