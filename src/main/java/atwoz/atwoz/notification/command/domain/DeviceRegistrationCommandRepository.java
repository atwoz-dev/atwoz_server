package atwoz.atwoz.notification.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRegistrationCommandRepository extends JpaRepository<DeviceRegistration, Long> {

    Optional<DeviceRegistration> findByMemberIdAndDeviceId(long memberId, String deviceId);

    Optional<DeviceRegistration> findByMemberIdAndIsActiveTrue(long memberId);

    void deleteByMemberId(long memberId);
}
