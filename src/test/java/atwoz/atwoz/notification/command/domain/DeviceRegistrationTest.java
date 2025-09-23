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

    @Test
    @DisplayName("refreshRegistrationToken(): 토큰 갱신 및 자동 활성화")
    void refreshRegistrationTokenUpdatesTokenAndActivates() {
        // given
        var reg = DeviceRegistration.of(1L, "device-123", "old-token");
        reg.deactivate();
        String newToken = "new-token-xyz";

        // when
        reg.refreshRegistrationToken(newToken);

        // then
        assertThat(reg.getRegistrationToken()).isEqualTo(newToken);
        assertThat(reg.isActive()).isTrue();
    }

    @Test
    @DisplayName("update(): 디바이스 ID와 토큰 갱신 및 activate() 호출")
    void updateUpdatesDeviceIdAndTokenAndCallsActivate() {
        // given
        var reg = DeviceRegistration.of(1L, "old-device", "old-token");
        reg.deactivate();
        String newDeviceId = "new-device-456";
        String newToken = "new-token-xyz";

        // when
        reg.update(newDeviceId, newToken);

        // then
        assertThat(reg.getDeviceId()).isEqualTo(newDeviceId);
        assertThat(reg.getRegistrationToken()).isEqualTo(newToken);
        assertThat(reg.isActive()).isTrue();
    }
}
