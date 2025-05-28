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
        deviceRegistrationCommandRepository.findByMemberIdAndDeviceId(memberId, request.deviceId())
            .ifPresentOrElse(
                deviceRegistration -> deviceRegistration.refreshRegistrationToken(request.registrationToken()),
                () -> {
                    var registration = DeviceRegistration.of(memberId, request.deviceId(), request.registrationToken());
                    deviceRegistrationCommandRepository.save(registration);
                });
    }
}
