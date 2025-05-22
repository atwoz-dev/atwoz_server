package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.NotificationPreference;
import atwoz.atwoz.notification.command.domain.NotificationPreferenceCommandRepository;
import atwoz.atwoz.notification.presentation.notificationsetting.DeviceTokenUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSettingService {

    private final NotificationPreferenceCommandRepository notificationSettingRepository;

    @Transactional
    public void create(long memberId) {
        if (notificationSettingRepository.existsByMemberId(memberId)) {
            throw new DuplicateNotificationSettingException(memberId);
        }
        notificationSettingRepository.save(NotificationPreference.of(memberId));
    }

    @Transactional
    public void updateDeviceToken(DeviceTokenUpdateRequest request, long memberId) {
        NotificationPreference notificationPreference = getNotificationSetting(memberId);
        notificationPreference.updateDeviceToken(request.deviceToken());
    }

    @Transactional
    public void optIn(long memberId) {
        NotificationPreference notificationPreference = getNotificationSetting(memberId);
        notificationPreference.optIn();
    }

    @Transactional
    public void optOut(long memberId) {
        NotificationPreference notificationPreference = getNotificationSetting(memberId);
        notificationPreference.optOut();
    }

    private NotificationPreference getNotificationSetting(long memberId) {
        return notificationSettingRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotificationSettingNotFoundException(memberId));
    }
}
