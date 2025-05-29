package atwoz.atwoz.notification.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.notification.command.application.NotificationPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static atwoz.atwoz.common.enums.StatusType.OK;

@Tag(name = "알림 설정 API")
@RestController
@RequestMapping("/notifications/preferences")
@RequiredArgsConstructor
public class NotificationPreferencesController {

    private final NotificationPreferenceService notificationPreferenceService;

    @Operation(summary = "알림 수신 전체 허용")
    @PatchMapping("/enable/global")
    public ResponseEntity<BaseResponse<Void>> enableGlobally(@AuthPrincipal AuthContext authContext) {
        notificationPreferenceService.enableGlobally(authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @Operation(summary = "특정 알림 타입 수신 허용")
    @PatchMapping("/enable/type")
    public ResponseEntity<BaseResponse<Void>> enableForType(
        @AuthPrincipal AuthContext authContext,
        @Valid @ModelAttribute NotificationPreferenceToggleRequest notificationPreferenceToggleRequest
    ) {
        notificationPreferenceService.enableForType(authContext.getId(), notificationPreferenceToggleRequest);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @Operation(summary = "알림 수신 전체 거부")
    @PatchMapping("/disable/global")
    public ResponseEntity<BaseResponse<Void>> disableGlobally(@AuthPrincipal AuthContext authContext) {
        notificationPreferenceService.disableGlobally(authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @Operation(summary = "특정 알림 타입 수신 거부")
    @PatchMapping("/disable/type")
    public ResponseEntity<BaseResponse<Void>> disableForType(
        @AuthPrincipal AuthContext authContext,
        @Valid @ModelAttribute NotificationPreferenceToggleRequest notificationPreferenceToggleRequest
    ) {
        notificationPreferenceService.disableForType(authContext.getId(), notificationPreferenceToggleRequest);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }
}
