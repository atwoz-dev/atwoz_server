package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.DeviceRegistrationCommandRepository;
import atwoz.atwoz.notification.command.domain.NotificationCommandRepository;
import atwoz.atwoz.notification.command.domain.NotificationPreferenceCommandRepository;
import atwoz.atwoz.notification.command.domain.NotificationTemplateCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationSendService 테스트")
class NotificationSendServiceTest {

    @Mock
    private NotificationCommandRepository notificationCommandRepository;

    @Mock
    private NotificationPreferenceCommandRepository notificationPreferenceCommandRepository;

    @Mock
    private NotificationTemplateCommandRepository notificationTemplateCommandRepository;

    @Mock
    private DeviceRegistrationCommandRepository deviceRegistrationCommandRepository;

    @Mock
    private NotificationSenderResolver notificationSenderResolver;

    @InjectMocks
    private NotificationSendService notificationSendService;


}
