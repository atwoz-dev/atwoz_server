package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.NotificationPreferenceCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationPreferenceService 테스트")
class NotificationPreferenceServiceTest {

    @Mock
    private NotificationPreferenceCommandRepository notificationSettingRepository;

    @InjectMocks
    private NotificationPreferenceService notificationPreferenceService;


}
