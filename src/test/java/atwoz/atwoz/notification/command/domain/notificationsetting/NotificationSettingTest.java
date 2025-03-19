package atwoz.atwoz.notification.command.domain.notificationsetting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("NotificationSetting 테스트")
class NotificationSettingTest {

    @Nested
    @DisplayName("of() 메서드 테스트")
    class OfMethodTest {
        @Test
        @DisplayName("주어진 memberId로 of() 호출 시, deviceToken은 null이고 optInStatus는 false인 NotificationSetting 생성됩니다.")
        void createNotificationSetting() {
            // given
            long memberId = 1L;

            // when
            NotificationSetting setting = NotificationSetting.of(memberId);

            // then
            assertThat(setting.getMemberId()).isEqualTo(memberId);
            assertThat(setting.getDeviceToken()).isNull();
            assertThat(setting.getIsOptedIn()).isFalse();
        }
    }

    @Nested
    @DisplayName("updateDeviceToken() 메서드 테스트")
    class UpdateDeviceTokenTest {
        @Test
        @DisplayName("유효한 deviceToken 값으로 업데이트 시 정상적으로 변경됩니다.")
        void updateDeviceToken() {
            // given
            NotificationSetting setting = NotificationSetting.of(1L);
            String token = "device_token_123";

            // when
            setting.updateDeviceToken(token);

            // then
            assertThat(setting.getDeviceToken()).isEqualTo(token);
        }

        @Test
        @DisplayName("null deviceToken 전달 시 NullPointerException이 발생합니다.")
        void updateDeviceTokenWithNullThrowsException() {
            // given
            NotificationSetting setting = NotificationSetting.of(1L);

            // when & then
            assertThatThrownBy(() -> setting.updateDeviceToken(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("optIn() 메서드 테스트")
    class OptInTest {
        @Test
        @DisplayName("optIn() 호출 시 optInStatus가 true로 변경됩니다.")
        void optInChangesStatus() {
            // given
            NotificationSetting setting = NotificationSetting.of(1L);

            // when
            setting.optIn();

            // then
            assertThat(setting.getIsOptedIn()).isTrue();
        }
    }

    @Nested
    @DisplayName("optOut() 메서드 테스트")
    class OptOutTest {
        @Test
        @DisplayName("optOut() 호출 시 optInStatus가 false로 변경됩니다.")
        void optOutChangesStatus() {
            // given
            NotificationSetting setting = NotificationSetting.of(1L);
            setting.optIn();

            // when
            setting.optOut();

            // then
            assertThat(setting.getIsOptedIn()).isFalse();
        }
    }
}
