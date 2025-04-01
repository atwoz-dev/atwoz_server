package atwoz.atwoz.notification.presentation.notification;

import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.notification.command.application.notification.NotificationSendService;
import atwoz.atwoz.notification.infra.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static atwoz.atwoz.common.enums.StatusType.OK;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationSendService notificationSendService;

    /**
     * 알림 테스트를 위한 메서드입니다.
     */
    // TODO: 테스트 후 삭제
    @PostMapping("/test")
    public ResponseEntity<BaseResponse<Void>> updateDeviceToken(
            @RequestBody NotificationRequest request
    ) {
        notificationSendService.send(request);
        return ResponseEntity.ok(BaseResponse.from(OK));
    }
}
