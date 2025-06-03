package atwoz.atwoz.admin.query.screening;

import atwoz.atwoz.admin.command.domain.screening.RejectionReasonType;
import atwoz.atwoz.admin.command.domain.screening.ScreeningStatus;
import atwoz.atwoz.member.command.domain.member.Gender;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

public record ScreeningView(
    long screeningId,
    String nickname,
    @Schema(implementation = Gender.class)
    String gender,
    String joinedDate,
    @Schema(implementation = ScreeningStatus.class)
    String screeningStatus,
    @Schema(implementation = RejectionReasonType.class)
    String rejectionReason
) {
    @QueryProjection
    public ScreeningView {
    }
}