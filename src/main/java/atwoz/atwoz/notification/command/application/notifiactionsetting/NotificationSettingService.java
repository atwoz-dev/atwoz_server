package atwoz.atwoz.notification.command.application.notifiactionsetting;

import atwoz.atwoz.notification.command.domain.notificationsetting.NotificationSetting;
import atwoz.atwoz.notification.command.domain.notificationsetting.NotificationSettingCommandRepository;
import atwoz.atwoz.notification.presentation.notificationsetting.DeviceTokenUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSettingService {

    private final NotificationSettingCommandRepository notificationSettingRepository;

    @Transactional
    public void create(long memberId) {
        if (notificationSettingRepository.existsByMemberId(memberId)) {
            throw new DuplicateNotificationSettingException(memberId);
        }
        notificationSettingRepository.save(NotificationSetting.of(memberId));
    }

    @Transactional
    public void updateDeviceToken(DeviceTokenUpdateRequest request, long memberId) {
        NotificationSetting notificationSetting = getNotificationSetting(memberId);
        notificationSetting.updateDeviceToken(request.deviceToken());
    }

    @Transactional
    public void optIn(long memberId) {
        NotificationSetting notificationSetting = getNotificationSetting(memberId);
        notificationSetting.optIn();
    }

    @Transactional
    public void optOut(long memberId) {
        NotificationSetting notificationSetting = getNotificationSetting(memberId);
        notificationSetting.optOut();
    }

    private NotificationSetting getNotificationSetting(long memberId) {
        return notificationSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NotificationSettingNotFoundException(memberId));
    }
}
