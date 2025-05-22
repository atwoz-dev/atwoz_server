package atwoz.atwoz.notification.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationCommandRepository extends JpaRepository<Notification, Long> {
}
