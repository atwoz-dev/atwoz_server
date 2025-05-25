package atwoz.atwoz.notification.presentation.notificationsetting;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.notification.command.application.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static atwoz.atwoz.common.enums.StatusType.OK;

@RestController
@RequestMapping("/notifications/settings")
@RequiredArgsConstructor
public class NotificationSettingController {

    private final NotificationPreferenceService notificationPreferenceService;

    @PatchMapping("/device-token")
    public ResponseEntity<BaseResponse<Void>> updateDeviceToken(
        @RequestBody DeviceTokenUpdateRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        notificationPreferenceService.updateDeviceToken(request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @PatchMapping("/opt-in")
    public ResponseEntity<BaseResponse<Void>> optIn(@AuthPrincipal AuthContext authContext) {
        notificationPreferenceService.enableGlobally(authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @PatchMapping("/opt-out")
    public ResponseEntity<BaseResponse<Void>> optOut(@AuthPrincipal AuthContext authContext) {
        notificationPreferenceService.disableGlobally(authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(OK));
    }
}
