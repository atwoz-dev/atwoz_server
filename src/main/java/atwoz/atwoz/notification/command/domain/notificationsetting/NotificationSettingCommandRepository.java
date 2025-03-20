package atwoz.atwoz.notification.command.domain.notificationsetting;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationSettingCommandRepository extends JpaRepository<NotificationSetting, Long> {

    boolean existsByMemberId(Long memberId);

    Optional<NotificationSetting> findByMemberId(Long memberId);
}
