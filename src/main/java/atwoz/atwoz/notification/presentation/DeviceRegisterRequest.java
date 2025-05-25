package atwoz.atwoz.notification.presentation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record DeviceRegisterRequest(
    @Schema(description = "디바이스 고유 ID", example = "device-uuid-1234")
    @NotBlank(message = "디바이스 ID를 입력해주세요.")
    String deviceId,

    @Schema(description = "FCM registration token", example = "fcm-registration-token")
    @NotBlank(message = "등록 토큰을 입력해주세요.")
    String registrationToken
) {
}
