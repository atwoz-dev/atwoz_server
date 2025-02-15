package atwoz.atwoz.admin.query;

import com.querydsl.core.annotations.QueryProjection;

public record ScreeningMemberView(
        String nickname,
        String gender,
        String joinedDate,
        String screeningStatus,
        String rejectionReason
) {
    @QueryProjection
    public ScreeningMemberView {
    }
}