package atwoz.atwoz.notification.presentation.notificationsetting;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.notification.command.application.notifiactionsetting.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static atwoz.atwoz.common.enums.StatusType.OK;

@RestController
@RequestMapping("/notifications/settings")
@RequiredArgsConstructor
public class NotificationSettingController {

    private final NotificationSettingService notificationSettingService;

    @PostMapping("/device-token")
    public ResponseEntity<BaseResponse<Void>> updateDeviceToken(
            @RequestBody DeviceTokenUpdateRequest request,
            @AuthPrincipal AuthContext authContext
    ) {
        notificationSettingService.updateDeviceToken(request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @PostMapping("/opt-in")
    public ResponseEntity<BaseResponse<Void>> optIn(@AuthPrincipal AuthContext authContext) {
        notificationSettingService.optIn(authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @PostMapping("/opt-out")
    public ResponseEntity<BaseResponse<Void>> optOut(@AuthPrincipal AuthContext authContext) {
        notificationSettingService.optOut(authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(OK));
    }
}
