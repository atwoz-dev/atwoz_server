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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceRegistrationServiceTest {

    @Mock
    DeviceRegistrationCommandRepository repository;

    @InjectMocks
    DeviceRegistrationService service;

    @Test
    @DisplayName("신규 디바이스 등록 시 저장 호출")
    void registerSavesNewRegistration() {
        // given
        long memberId = 1L;
        var request = new DeviceRegisterRequest("device-123", "token-abc");
        when(repository.findByMemberIdAndDeviceId(memberId, request.deviceId())).thenReturn(Optional.empty());

        // when
        service.register(memberId, request);

        // then
        verify(repository).save(argThat(reg ->
            reg.getRegistrationToken().equals(request.registrationToken()) && reg.isActive()
        ));
    }

    @Test
    @DisplayName("중복 디바이스 등록 시 예외 발생")
    void registerThrowsWhenDuplicate() {
        // given
        long memberId = 2L;
        var request = new DeviceRegisterRequest("device-xyz", "token-123");
        when(repository.findByMemberIdAndDeviceId(memberId, request.deviceId()))
            .thenReturn(Optional.of(DeviceRegistration.of(memberId, request.deviceId(), request.registrationToken())));

        // when && then
        assertThatThrownBy(() -> service.register(memberId, request))
            .isInstanceOf(DuplicateDeviceRegistrationException.class);

        verify(repository, never()).save(any());
    }
}
