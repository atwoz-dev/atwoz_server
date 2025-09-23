package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.DeviceRegistration;
import atwoz.atwoz.notification.command.domain.DeviceRegistrationCommandRepository;
import atwoz.atwoz.notification.presentation.DeviceRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceRegistrationService {

    private final DeviceRegistrationCommandRepository deviceRegistrationCommandRepository;

    /**
     * 디바이스를 등록하거나 토큰을 갱신합니다.
     * 현재 단일 기기만 지원하므로, 기존 디바이스가 있으면 갱신하고 없으면 새로 등록합니다.
     *
     * @param memberId 회원 ID
     * @param request  디바이스 등록 요청 (deviceId: 기기 고유 식별자, registrationToken: FCM 토큰)
     */
    @Transactional
    public void register(long memberId, DeviceRegisterRequest request) {
        deviceRegistrationCommandRepository.findByMemberId(memberId)
            .ifPresentOrElse(
                device -> {
                    device.update(request.deviceId(), request.registrationToken());
                    log.info("사용자(id: {})의 디바이스 등록 갱신", memberId);
                },
                () -> {
                    var registration = DeviceRegistration.of(memberId, request.deviceId(), request.registrationToken());
                    deviceRegistrationCommandRepository.save(registration);
                    log.info("사용자(id: {})의 디바이스 등록이 존재하지 않아 새 디바이스 등록", memberId);
                }
            );
    }

    /**
     * 회원의 디바이스 등록을 활성화합니다.
     * 계정 복구 시 호출됩니다.
     */
    @Transactional
    public void activateByMemberId(long memberId) {
        deviceRegistrationCommandRepository.findByMemberId(memberId)
            .ifPresentOrElse(
                DeviceRegistration::activate,
                () -> log.info("사용자(id: {})의 디바이스 등록이 존재하지 않아 활성화 실패", memberId)
            );
    }

    /**
     * 회원의 활성화된 디바이스 등록을 비활성화합니다.
     * 로그아웃이나 휴면 상태 전환 시 호출됩니다.
     */
    @Transactional
    public void deactivateByMemberId(long memberId) {
        deviceRegistrationCommandRepository.findByMemberIdAndIsActiveTrue(memberId)
            .ifPresentOrElse(
                DeviceRegistration::deactivate,
                () -> log.info("사용자(id: {})의 활성화된 디바이스 등록이 존재하지 않아 비활성화 실패", memberId)
            );
    }
}
