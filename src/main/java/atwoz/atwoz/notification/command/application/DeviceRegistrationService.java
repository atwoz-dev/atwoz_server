package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.DeviceRegistration;
import atwoz.atwoz.notification.command.domain.DeviceRegistrationCommandRepository;
import atwoz.atwoz.notification.presentation.DeviceRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeviceRegistrationService {

    private final DeviceRegistrationCommandRepository deviceRegistrationCommandRepository;

    @Transactional
    public void register(long memberId, DeviceRegisterRequest request) {
        boolean alreadyRegistered = deviceRegistrationCommandRepository
            .findByMemberIdAndDeviceId(memberId, request.deviceId())
            .isPresent();

        if (alreadyRegistered) {
            throw new DuplicateDeviceRegistrationException(request.deviceId());
        }

        var registration = DeviceRegistration.of(memberId, request.deviceId(), request.registrationToken());
        deviceRegistrationCommandRepository.save(registration);
    }
}
