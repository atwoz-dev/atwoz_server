package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.NotificationCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationDeleteService {

    private final NotificationCommandRepository notificationCommandRepository;

    @Transactional
    public void delete(NotificationDeleteRequest request) {
        notificationCommandRepository.deleteAllByIdIn(request.notificationIds());
    }
}
