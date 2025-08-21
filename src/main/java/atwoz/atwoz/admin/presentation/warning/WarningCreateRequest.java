package atwoz.atwoz.admin.presentation.warning;

import atwoz.atwoz.admin.command.domain.warning.WarningReasonType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record WarningCreateRequest(
    long memberId,

    @Schema(implementation = WarningReasonType.class)
    @NotEmpty
    Set<String> reasonTypes,

    @Schema(description = "경고 여부 (true: 경고, false: 권고)")
    boolean isCritical
) {
}
