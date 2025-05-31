package atwoz.atwoz.admin.command.application.suspension;

import atwoz.atwoz.admin.command.domain.suspension.SuspensionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SuspendRequest(
    @Schema(description = "정지 상태", implementation = SuspensionStatus.class)
    @NotBlank(message = "정지 상태를 입력해주세요.")
    String status
) {
}
