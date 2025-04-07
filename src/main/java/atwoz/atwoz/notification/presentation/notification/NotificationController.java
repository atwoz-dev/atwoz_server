package atwoz.atwoz.notification.presentation.notification;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.notification.command.application.notification.NotificationReadService;
import atwoz.atwoz.notification.command.application.notification.NotificationSendService;
import atwoz.atwoz.notification.infra.notification.NotificationRequest;
import atwoz.atwoz.notification.query.NotificationQueryRepository;
import atwoz.atwoz.notification.query.NotificationView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static atwoz.atwoz.common.enums.StatusType.OK;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationSendService notificationSendService;
    private final NotificationReadService notificationReadService;
    private final NotificationQueryRepository notificationQueryRepository;

    /**
     * 알림 테스트를 위한 메서드입니다.
     */
    // TODO: 테스트 후 삭제
    @PostMapping("/test")
    public ResponseEntity<BaseResponse<Void>> test(@RequestBody NotificationRequest request) {
        notificationSendService.send(request);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<BaseResponse<Void>> markAsRead(@PathVariable long notificationId) {
        notificationReadService.markAsRead(notificationId);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<NotificationView>>> getNotifications(
            @RequestParam(required = false, defaultValue = "false") boolean isRead,
            @AuthPrincipal AuthContext authContext
    ) {
        return ResponseEntity.ok(
                BaseResponse.of(OK, notificationQueryRepository.findNotifications(authContext.getId(), isRead))
        );
    }
}
