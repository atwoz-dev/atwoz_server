package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.NotificationPreference;
import atwoz.atwoz.notification.command.domain.NotificationPreferenceCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void disableGlobally(long memberId) {
        getNotificationPreference(memberId).disableGlobally();
    }

    private NotificationPreference getNotificationPreference(long memberId) {
        return notificationPreferenceRepository.findByMemberId(memberId)
            .orElseThrow(() -> new NotificationPreferenceNotFoundException(memberId));
    }
}
