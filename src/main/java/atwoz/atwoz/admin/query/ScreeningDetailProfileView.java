package atwoz.atwoz.admin.query;

import com.querydsl.core.annotations.QueryProjection;

public record ScreeningDetailProfileView(
    long memberId,
    String screeningStatus,
    String rejectionReason,
    String nickname,
    int age,
    String gender,
    String joinedDate
) {
    @QueryProjection
    public ScreeningDetailProfileView {
    }
}
