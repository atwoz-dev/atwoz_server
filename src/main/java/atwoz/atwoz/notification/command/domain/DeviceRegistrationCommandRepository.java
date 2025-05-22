package atwoz.atwoz.notification.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRegistrationCommandRepository extends JpaRepository<DeviceRegistration, Long> {
    List<DeviceRegistration> findByMemberIdAndActiveTrue(long memberId);
}
