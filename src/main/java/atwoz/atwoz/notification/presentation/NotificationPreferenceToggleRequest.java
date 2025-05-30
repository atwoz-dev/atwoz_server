package atwoz.atwoz.notification.presentation;

import atwoz.atwoz.notification.command.domain.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record NotificationPreferenceToggleRequest(
    @Schema(
        description = "알림 타입",
        example = "LIKE",
        implementation = NotificationType.class
    )
    @NotBlank(message = "알림 타입을 입력해주세요.")
    String type
) {
}
