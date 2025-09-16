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

    /**
     * 디바이스를 등록하거나 토큰을 갱신합니다.
     * 단일 기기만 지원하므로, 새로운 기기 등록 시 기존 모든 기기를 삭제합니다.
     *
     * @param memberId 회원 ID
     * @param request  디바이스 등록 요청 (deviceId: 기기 고유 식별자, registrationToken: FCM 토큰)
     */
    @Transactional
    public void register(long memberId, DeviceRegisterRequest request) {
        deviceRegistrationCommandRepository.findByMemberIdAndDeviceId(memberId, request.deviceId())
            .ifPresentOrElse(
                // 기존 디바이스가 있으면 FCM 토큰만 갱신
                device -> device.refreshRegistrationToken(request.registrationToken()),
                // 새로운 디바이스면 기존 모든 디바이스 삭제 후 등록
                () -> registerNewDevice(memberId, request)
            );
    }

    /**
     * 새로운 디바이스를 등록합니다.
     * 기존 모든 디바이스를 삭제한 후 새 디바이스를 등록하여 단일 기기만 유지합니다.
     */
    private void registerNewDevice(long memberId, DeviceRegisterRequest request) {
        deviceRegistrationCommandRepository.deleteByMemberId(memberId);
        var registration = DeviceRegistration.of(memberId, request.deviceId(), request.registrationToken());
        deviceRegistrationCommandRepository.save(registration);
    }
}
