package atwoz.atwoz.admin.query.member;

import atwoz.atwoz.member.command.domain.member.ActivityStatus;
import atwoz.atwoz.member.command.domain.member.Gender;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberView(
    long memberId,
    String nickname,
    @Schema(implementation = Gender.class)
    String gender,
    @Schema(implementation = ActivityStatus.class)
    String activityStatus,
    String joinedAt,
    int warningCount,
    String bannedReason
) {
    @QueryProjection
    public MemberView {
    }
}
