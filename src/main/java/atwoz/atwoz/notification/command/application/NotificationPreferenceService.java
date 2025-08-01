package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.NotificationPreference;
import atwoz.atwoz.notification.command.domain.NotificationPreferenceCommandRepository;
import atwoz.atwoz.notification.command.domain.NotificationType;
import atwoz.atwoz.notification.presentation.NotificationPreferenceSetRequest;
import atwoz.atwoz.notification.presentation.NotificationPreferenceToggleRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

import static atwoz.atwoz.notification.command.application.NotificationTypeMapper.toNotificationType;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {

    private final NotificationPreferenceCommandRepository notificationPreferenceRepository;

    @Transactional
    public void create(long memberId) {
        if (notificationPreferenceRepository.existsByMemberId(memberId)) {
            throw new DuplicateNotificationPreferenceException(memberId);
        }
        notificationPreferenceRepository.save(NotificationPreference.of(memberId));
    }

    @Transactional
    public void enableGlobally(long memberId) {
        getNotificationPreference(memberId).enableGlobally();
    }

    @Transactional
    public void enableForType(long memberId, NotificationPreferenceToggleRequest request) {
        getNotificationPreference(memberId).enableForNotificationType(toNotificationType(request.type()));
    }

    @Transactional
    public void disableGlobally(long memberId) {
        getNotificationPreference(memberId).disableGlobally();
    }

    @Transactional
    public void disableForType(long memberId, NotificationPreferenceToggleRequest request) {
        getNotificationPreference(memberId).disableForNotificationType(toNotificationType(request.type()));
    }

    @Transactional
    public void setNotificationPreferences(Long memberId, NotificationPreferenceSetRequest request) {
        var preference = getNotificationPreference(memberId);

        Map<NotificationType, Boolean> preferences = request.preferences().entrySet().stream()
            .collect(Collectors.toMap(
                entry -> toNotificationType(entry.getKey()),
                Map.Entry::getValue
            ));

        preference.updateNotificationPreferences(preferences);
    }

    private NotificationPreference getNotificationPreference(long memberId) {
        return notificationPreferenceRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotificationPreferenceNotFoundException(memberId));
    }
}
