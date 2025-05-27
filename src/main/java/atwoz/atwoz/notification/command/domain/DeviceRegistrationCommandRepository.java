package atwoz.atwoz.notification.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRegistrationCommandRepository extends JpaRepository<DeviceRegistration, Long> {
    List<DeviceRegistration> findByMemberIdAndIsActiveTrue(long memberId);

    Optional<DeviceRegistration> findByMemberIdAndDeviceId(Long memberId, String deviceId);
}
