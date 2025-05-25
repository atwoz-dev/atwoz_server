package atwoz.atwoz.notification.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.notification.command.application.DeviceRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static atwoz.atwoz.common.enums.StatusType.OK;

@Tag(name = "기기 id 설정 API")
@RestController
@RequestMapping("/notifications/device-registration")
@RequiredArgsConstructor
public class DeviceRegistrationController {

    private final DeviceRegistrationService deviceRegistrationService;

    @Operation(summary = "기기 등록")
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> registerDevice(
        @Valid @RequestBody DeviceRegisterRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        deviceRegistrationService.register(authContext.getId(), request);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }
}
