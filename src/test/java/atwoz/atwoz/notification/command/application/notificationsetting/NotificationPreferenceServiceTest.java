package atwoz.atwoz.notification.command.application.notificationsetting;

import atwoz.atwoz.notification.command.application.DuplicateNotificationPreferenceException;
import atwoz.atwoz.notification.command.application.NotificationPreferenceNotFoundException;
import atwoz.atwoz.notification.command.application.NotificationPreferenceService;
import atwoz.atwoz.notification.command.domain.NotificationPreference;
import atwoz.atwoz.notification.command.domain.NotificationPreferenceCommandRepository;
import atwoz.atwoz.notification.presentation.notificationsetting.DeviceTokenUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationPreferenceService 테스트")
class NotificationPreferenceServiceTest {

    @Mock
    private NotificationPreferenceCommandRepository notificationSettingRepository;

    @InjectMocks
    private NotificationPreferenceService notificationPreferenceService;

    @Nested
    @DisplayName("create() 메서드 테스트")
    class CreateTest {

        @Test
        @DisplayName("NotificationSetting이 존재하지 않으면 생성합니다다.")
        void createNewNotificationSetting() {
            // given
            long memberId = 1L;
            when(notificationSettingRepository.existsByMemberId(memberId)).thenReturn(false);

            // when
            notificationPreferenceService.create(memberId);

            // then
            verify(notificationSettingRepository).save(any(NotificationPreference.class));
        }

        @Test
        @DisplayName("NotificationSetting이 이미 존재하면 예외를 던집니다.")
        void createExistingNotificationSettingThrowsException() {
            // given
            long memberId = 1L;
            when(notificationSettingRepository.existsByMemberId(memberId)).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> notificationPreferenceService.create(memberId))
                .isInstanceOf(DuplicateNotificationPreferenceException.class);
        }
    }

    @Nested
    @DisplayName("updateDeviceToken() 메서드 테스트")
    class UpdateDeviceTokenTest {

        @Test
        @DisplayName("NotificationSetting이 존재하면 deviceToken을 업데이트합니다.")
        void updateDeviceTokenSuccess() {
            // given
            long memberId = 1L;
            String newToken = "new_device_token";
            DeviceTokenUpdateRequest request = new DeviceTokenUpdateRequest(newToken);
            NotificationPreference setting = NotificationPreference.of(memberId);
            when(notificationSettingRepository.findByMemberId(memberId)).thenReturn(Optional.of(setting));

            // when
            notificationPreferenceService.updateDeviceToken(request, memberId);

            // then
            assertThat(setting.getDeviceToken()).isEqualTo(newToken);
        }

        @Test
        @DisplayName("NotificationSetting이 존재하지 않으면 예외를 발생시킵니다.")
        void updateDeviceTokenNotFound() {
            // given
            long memberId = 1L;
            String newToken = "new_device_token";
            DeviceTokenUpdateRequest request = new DeviceTokenUpdateRequest(newToken);
            when(notificationSettingRepository.findByMemberId(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> notificationPreferenceService.updateDeviceToken(request, memberId))
                .isInstanceOf(NotificationPreferenceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("optIn() 메서드 테스트")
    class OptInTest {

        @Test
        @DisplayName("NotificationSetting이 존재하면 optIn을 수행합니다.")
        void optInSuccess() {
            // given
            long memberId = 1L;
            NotificationPreference setting = NotificationPreference.of(memberId);
            when(notificationSettingRepository.findByMemberId(memberId)).thenReturn(Optional.of(setting));

            // when
            notificationPreferenceService.enableGlobally(memberId);

            // then
            assertThat(setting.isOptedIn()).isTrue();
        }

        @Test
        @DisplayName("NotificationSetting이 존재하지 않으면 예외를 발생시킵니다.")
        void optInNotFound() {
            // given
            long memberId = 1L;
            when(notificationSettingRepository.findByMemberId(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> notificationPreferenceService.enableGlobally(memberId))
                .isInstanceOf(NotificationPreferenceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("optOut() 메서드 테스트")
    class OptOutTest {

        @Test
        @DisplayName("NotificationSetting이 존재하면 optOut을 수행합니다.")
        void optOutSuccess() {
            // given
            long memberId = 1L;
            NotificationPreference setting = NotificationPreference.of(memberId);
            // 먼저 optIn을 호출하여 true 상태로 만든 후 optOut 테스트
            setting.optIn();
            when(notificationSettingRepository.findByMemberId(memberId)).thenReturn(Optional.of(setting));

            // when
            notificationPreferenceService.disableGlobally(memberId);

            // then
            assertThat(setting.isOptedIn()).isFalse();
        }

        @Test
        @DisplayName("NotificationSetting이 존재하지 않으면 예외를 발생시킵니다.")
        void optOutNotFound() {
            // given
            long memberId = 1L;
            when(notificationSettingRepository.findByMemberId(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> notificationPreferenceService.disableGlobally(memberId))
                .isInstanceOf(NotificationPreferenceNotFoundException.class);
        }
    }
}
