package atwoz.atwoz.admin.query;

import com.querydsl.core.annotations.QueryProjection;

public record ScreeningMemberData(
        String nickname,
        String gender,
        String screeningStatus,
        String joinedDate,
        int warningCount,
        String activityStatus,
        String bannedReason
) {
    @QueryProjection
    public ScreeningMemberData {
    }
}