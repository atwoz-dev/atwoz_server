package atwoz.atwoz.admin.query.screening;

import atwoz.atwoz.admin.command.domain.screening.RejectionReasonType;
import atwoz.atwoz.admin.command.domain.screening.ScreeningStatus;
import atwoz.atwoz.member.command.domain.member.Gender;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

public record ScreeningDetailProfileView(
    long memberId,
    @Schema(implementation = ScreeningStatus.class)
    String screeningStatus,
    @Schema(implementation = RejectionReasonType.class)
    String rejectionReason,
    String nickname,
    int age,
    @Schema(implementation = Gender.class)
    String gender,
    String joinedDate
) {
    @QueryProjection
    public ScreeningDetailProfileView {
    }
}
