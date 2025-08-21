package atwoz.atwoz.admin.presentation.warning;

import atwoz.atwoz.admin.command.domain.warning.WarningReasonType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record WarningCreateRequest(
    long memberId,

    @Schema(implementation = WarningReasonType.class)
    @NotEmpty
    List<String> reasonTypes
) {
}
