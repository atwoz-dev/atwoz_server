package atwoz.atwoz.notification.presentation;

import atwoz.atwoz.auth.presentation.AuthContext;
import atwoz.atwoz.auth.presentation.AuthPrincipal;
import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.notification.command.application.NotificationReadService;
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

    private final NotificationReadService notificationReadService;
    private final NotificationQueryRepository notificationQueryRepository;

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
