package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.Notification;
import atwoz.atwoz.notification.command.domain.NotificationCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationReadService {

    private final NotificationCommandRepository notificationCommandRepository;

    @Transactional
    public void markAsRead(NotificationReadRequest request) {
        notificationCommandRepository.findAllById(request.notificationIds())
            .forEach(Notification::markAsRead);
    }
}