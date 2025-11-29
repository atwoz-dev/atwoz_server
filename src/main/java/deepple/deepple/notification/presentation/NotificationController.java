package deepple.deepple.notification.presentation;

import deepple.deepple.auth.presentation.AuthContext;
import deepple.deepple.auth.presentation.AuthPrincipal;
import deepple.deepple.common.response.BaseResponse;
import deepple.deepple.notification.command.application.*;
import deepple.deepple.notification.query.NotificationQueryService;
import deepple.deepple.notification.query.NotificationViews;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static deepple.deepple.common.enums.StatusType.OK;

@Tag(name = "알림 API")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationReadService notificationReadService;
    private final NotificationDeleteService notificationDeleteService;
    private final NotificationQueryService notificationQueryService;

    // TODO: 삭제 필요(테스트 용도)
    private final NotificationSendService notificationSendService;

    @Operation(summary = "알림 읽기")
    @PatchMapping
    public ResponseEntity<BaseResponse<Void>> markAsRead(
        @Validated @RequestBody NotificationReadRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        notificationReadService.markAsRead(request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    @Operation(summary = "알림 목록 조회")
    @GetMapping
    public ResponseEntity<BaseResponse<NotificationViews>> getNotifications(
        @AuthPrincipal AuthContext authContext,
        @RequestParam(required = false) Long lastId
    ) {
        return ResponseEntity.ok(
            BaseResponse.of(OK, notificationQueryService.findNotifications(authContext.getId(), lastId))
        );
    }

    @Operation(summary = "알림 삭제")
    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> delete(
        @Validated @RequestBody NotificationDeleteRequest request,
        @AuthPrincipal AuthContext authContext
    ) {
        notificationDeleteService.delete(request, authContext.getId());
        return ResponseEntity.ok(BaseResponse.from(OK));
    }

    // TODO: 삭제 필요(테스트 용도)
    @Operation(summary = "(테스트) 알림 전송")
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> send(
        @RequestBody NotificationSendRequest request
    ) {
        notificationSendService.send(request);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }
}
