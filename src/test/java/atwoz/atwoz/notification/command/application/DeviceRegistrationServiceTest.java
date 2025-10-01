package atwoz.atwoz.notification.command.application;

import atwoz.atwoz.notification.command.domain.DeviceRegistration;
import atwoz.atwoz.notification.command.domain.DeviceRegistrationCommandRepository;
import atwoz.atwoz.notification.presentation.DeviceRegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceRegistrationServiceTest {

    @Mock
    DeviceRegistrationCommandRepository repository;

    @InjectMocks
    DeviceRegistrationService service;

    @Test
    @DisplayName("신규 디바이스 등록 시 새로운 DeviceRegistration 저장")
    void registerSavesNewRegistrationWhenNotExists() {
        // given
        long memberId = 1L;
        var request = new DeviceRegisterRequest("device-123", "token-abc");
        when(repository.findByMemberId(memberId)).thenReturn(Optional.empty());

        // when
        service.register(memberId, request);

        // then
        verify(repository).save(argThat(reg ->
            reg.getRegistrationToken().equals(request.registrationToken()) &&
                reg.getDeviceId().equals(request.deviceId()) &&
                reg.isActive()
        ));
    }

    @Test
    @DisplayName("기존 디바이스 존재 시 update 메서드 호출")
    void registerUpdatesWhenDeviceExists() {
        // given
        long memberId = 2L;
        var request = new DeviceRegisterRequest("new-device-456", "new-token");
        var existing = spy(DeviceRegistration.of(memberId, "old-device", "old-token"));
        when(repository.findByMemberId(memberId)).thenReturn(Optional.of(existing));

        // when
        service.register(memberId, request);

        // then
        verify(existing).update(request.deviceId(), request.registrationToken());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("activateByMemberId(): 기존 디바이스가 있으면 활성화")
    void activateByMemberIdActivatesExistingDevice() {
        // given
        long memberId = 3L;
        var existing = spy(DeviceRegistration.of(memberId, "device-123", "token"));
        existing.deactivate();
        when(repository.findByMemberId(memberId)).thenReturn(Optional.of(existing));

        // when
        service.activateByMemberId(memberId);

        // then
        verify(existing).activate();
    }

    @Test
    @DisplayName("activateByMemberId(): 기존 디바이스가 없으면 로그만 출력")
    void activateByMemberIdLogsWhenDeviceNotExists() {
        // given
        long memberId = 4L;
        when(repository.findByMemberId(memberId)).thenReturn(Optional.empty());

        // when
        service.activateByMemberId(memberId);

        // then
        verify(repository).findByMemberId(memberId);
    }

    @Test
    @DisplayName("deactivateByMemberId(): 활성화된 디바이스가 있으면 비활성화")
    void deactivateByMemberIdDeactivatesActiveDevice() {
        // given
        long memberId = 5L;
        var existing = spy(DeviceRegistration.of(memberId, "device-123", "token"));
        when(repository.findByMemberIdAndIsActiveTrue(memberId)).thenReturn(Optional.of(existing));

        // when
        service.deactivateByMemberId(memberId);

        // then
        verify(existing).deactivate();
    }

    @Test
    @DisplayName("deactivateByMemberId(): 활성화된 디바이스가 없으면 로그만 출력")
    void deactivateByMemberIdLogsWhenNoActiveDevice() {
        // given
        long memberId = 6L;
        when(repository.findByMemberIdAndIsActiveTrue(memberId)).thenReturn(Optional.empty());

        // when
        service.deactivateByMemberId(memberId);

        // then
        verify(repository).findByMemberIdAndIsActiveTrue(memberId);
    }
}
