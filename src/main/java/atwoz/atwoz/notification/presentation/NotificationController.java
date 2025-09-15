package atwoz.atwoz.notification.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.notification.command.application.NotificationReadRequest;
import atwoz.atwoz.notification.command.application.NotificationReadService;
import atwoz.atwoz.notification.command.application.NotificationSendRequest;
import atwoz.atwoz.notification.command.application.NotificationSendService;
import atwoz.atwoz.notification.query.NotificationQueryRepository;
import atwoz.atwoz.notification.query.NotificationView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static atwoz.atwoz.common.enums.StatusType.OK;

@Tag(name = "알림 API")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationReadService notificationReadService;
    private final NotificationQueryRepository notificationQueryRepository;

    // TODO: 삭제 필요(테스트 용도)
    private final NotificationSendService notificationSendService;

    @Operation(summary = "알림 읽기")
    @PatchMapping
    public ResponseEntity<BaseResponse<Void>> markAsRead(@RequestBody NotificationReadRequest request) {
        notificationReadService.markAsRead(request);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @Operation(summary = "읽지 않은 알림 조회")
    @GetMapping
    public ResponseEntity<BaseResponse<List<NotificationView>>> getNotifications(
        @RequestParam(required = false, defaultValue = "false") boolean isRead,
        @AuthPrincipal AuthContext authContext
    ) {
        return ResponseEntity.ok(
            BaseResponse.of(OK, notificationQueryRepository.findNotifications(authContext.getId(), isRead))
        );
    }

    // TODO: 삭제 필요(테스트 용도)
    @Operation(summary = "알림 전송 테스트")
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> send(
        @RequestBody NotificationSendRequest request
    ) {
        notificationSendService.send(request);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }
}
