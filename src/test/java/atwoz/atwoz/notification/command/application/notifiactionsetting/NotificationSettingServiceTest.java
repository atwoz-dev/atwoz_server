package atwoz.atwoz.notification.command.application.notifiactionsetting;

import atwoz.atwoz.notification.command.domain.notificationsetting.NotificationSetting;
import atwoz.atwoz.notification.command.domain.notificationsetting.NotificationSettingCommandRepository;
import atwoz.atwoz.notification.presentation.notificationsetting.DeviceTokenUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationSettingService 테스트")
class NotificationSettingServiceTest {

    @Mock
    private NotificationSettingCommandRepository notificationSettingRepository;

    @InjectMocks
    private NotificationSettingService notificationSettingService;

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
            notificationSettingService.create(memberId);

            // then
            verify(notificationSettingRepository).save(any(NotificationSetting.class));
        }

        @Test
        @DisplayName("이미 NotificationSetting이 존재하면 생성하지 않습니다.")
        void createExistingNotificationSetting() {
            // given
            long memberId = 1L;
            when(notificationSettingRepository.existsByMemberId(memberId)).thenReturn(true);

            // when
            notificationSettingService.create(memberId);

            // then
            verify(notificationSettingRepository, never()).save(any(NotificationSetting.class));
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
            NotificationSetting setting = NotificationSetting.of(memberId);
            when(notificationSettingRepository.findByMemberId(memberId)).thenReturn(Optional.of(setting));

            // when
            notificationSettingService.updateDeviceToken(request, memberId);

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
            assertThatThrownBy(() -> notificationSettingService.updateDeviceToken(request, memberId))
                    .isInstanceOf(NotificationSettingNotFoundException.class);
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
            NotificationSetting setting = NotificationSetting.of(memberId);
            when(notificationSettingRepository.findByMemberId(memberId)).thenReturn(Optional.of(setting));

            // when
            notificationSettingService.optIn(memberId);

            // then
            assertThat(setting.getIsOptedIn()).isTrue();
        }

        @Test
        @DisplayName("NotificationSetting이 존재하지 않으면 예외를 발생시킵니다.")
        void optInNotFound() {
            // given
            long memberId = 1L;
            when(notificationSettingRepository.findByMemberId(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> notificationSettingService.optIn(memberId))
                    .isInstanceOf(NotificationSettingNotFoundException.class);
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
            NotificationSetting setting = NotificationSetting.of(memberId);
            // 먼저 optIn을 호출하여 true 상태로 만든 후 optOut 테스트
            setting.optIn();
            when(notificationSettingRepository.findByMemberId(memberId)).thenReturn(Optional.of(setting));

            // when
            notificationSettingService.optOut(memberId);

            // then
            assertThat(setting.getIsOptedIn()).isFalse();
        }

        @Test
        @DisplayName("NotificationSetting이 존재하지 않으면 예외를 발생시킵니다.")
        void optOutNotFound() {
            // given
            long memberId = 1L;
            when(notificationSettingRepository.findByMemberId(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> notificationSettingService.optOut(memberId))
                    .isInstanceOf(NotificationSettingNotFoundException.class);
        }
    }
}
