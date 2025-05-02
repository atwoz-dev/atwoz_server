package atwoz.atwoz.notification.command.application.notification;

import atwoz.atwoz.notification.command.domain.notification.Notification;
import atwoz.atwoz.notification.command.domain.notification.NotificationCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationReadService {

    private final NotificationCommandRepository notificationCommandRepository;

    @Transactional
    public void markAsRead(long notificationId) {
        Notification notification = getNotification(notificationId);
        notification.markAsRead();
    }

    private Notification getNotification(long notificationId) {
        return notificationCommandRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException(notificationId));
    }
}