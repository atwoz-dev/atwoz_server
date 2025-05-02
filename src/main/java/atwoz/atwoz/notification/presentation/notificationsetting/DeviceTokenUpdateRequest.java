package atwoz.atwoz.notification.presentation.notificationsetting;

import jakarta.validation.constraints.NotBlank;

public record DeviceTokenUpdateRequest(
    @NotBlank(message = "Device token 값은 비어있을 수 없습니다.")
    String deviceToken
) {
}
