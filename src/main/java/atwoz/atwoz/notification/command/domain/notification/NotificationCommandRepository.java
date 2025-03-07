package atwoz.atwoz.notification.command.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationCommandRepository extends JpaRepository<Notification, Long> {
}
