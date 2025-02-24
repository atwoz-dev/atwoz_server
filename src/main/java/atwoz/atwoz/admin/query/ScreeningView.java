package atwoz.atwoz.admin.query;

import com.querydsl.core.annotations.QueryProjection;

public record ScreeningView(
        long screeningId,
        String nickname,
        String gender,
        String joinedDate,
        String screeningStatus,
        String rejectionReason
) {
    @QueryProjection
    public ScreeningView {
    }
}