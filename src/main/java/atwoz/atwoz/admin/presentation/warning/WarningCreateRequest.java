package atwoz.atwoz.admin.presentation.warning;

import jakarta.validation.constraints.NotNull;

public record WarningCreateRequest(
    long memberId,

    @NotNull
    WarningReasonRequest reasonType
) {
}
