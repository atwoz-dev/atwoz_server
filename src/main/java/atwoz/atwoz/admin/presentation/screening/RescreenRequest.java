package atwoz.atwoz.admin.presentation.screening;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RescreenRequest(
    @Schema(description = "멤버 ID", example = "1")
    @NotNull(message = "멤버 ID는 필수입니다.")
    Long memberId
) {
}