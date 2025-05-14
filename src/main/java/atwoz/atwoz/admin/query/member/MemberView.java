package atwoz.atwoz.admin.query.member;

import com.querydsl.core.annotations.QueryProjection;

public record MemberView(
    long memberId,
    String nickname,
    String gender,
    String activityStatus,
    String joinedAt,
    int warningCount,
    String bannedReason
) {
    @QueryProjection
    public MemberView {
    }
}
