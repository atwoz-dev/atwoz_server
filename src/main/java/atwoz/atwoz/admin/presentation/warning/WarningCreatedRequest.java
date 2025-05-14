package atwoz.atwoz.admin.presentation.warning;

public record WarningCreatedRequest(
    long memberId,
    WarningReasonRequest reasonType
) {
}
