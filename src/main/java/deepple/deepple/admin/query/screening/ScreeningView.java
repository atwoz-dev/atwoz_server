package deepple.deepple.admin.query.screening;

import com.querydsl.core.annotations.QueryProjection;
import deepple.deepple.admin.command.domain.screening.RejectionReasonType;
import deepple.deepple.admin.command.domain.screening.ScreeningStatus;
import deepple.deepple.member.command.domain.member.Gender;
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