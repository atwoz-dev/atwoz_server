package atwoz.atwoz.notification.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.notification.command.application.NotificationPreferenceService;
import atwoz.atwoz.notification.query.NotificationPreferenceQueryRepository;
import atwoz.atwoz.notification.query.NotificationPreferenceView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static atwoz.atwoz.common.enums.StatusType.OK;

@Tag(name = "알림 설정 API")
@RestController
@RequestMapping("/notifications/preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationPreferenceService notificationPreferenceService;
    private final NotificationPreferenceQueryRepository notificationPreferenceQueryRepository;

    @Operation(summary = "알림 설정 전체 조회")
    @GetMapping
    public ResponseEntity<BaseResponse<NotificationPreferenceView>> getNotificationPreference(
        @AuthPrincipal AuthContext authContext
    ) {
        var preference = notificationPreferenceQueryRepository.findByMemberId(authContext.getId()).orElseThrow();
        return ResponseEntity.ok(BaseResponse.of(OK, preference));
    }

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

    @Operation(summary = "원하는 알림 토글")
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> setNotificationPreference(
        @AuthPrincipal AuthContext authContext,
        @Valid @RequestBody NotificationPreferenceSetRequest notificationPreferenceSetRequest
    ) {
        notificationPreferenceService.setNotificationPreferences(authContext.getId(), notificationPreferenceSetRequest);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }
}
