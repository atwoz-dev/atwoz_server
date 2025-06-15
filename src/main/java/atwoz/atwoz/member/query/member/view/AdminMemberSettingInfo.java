package atwoz.atwoz.member.query.member.view;

import atwoz.atwoz.member.command.domain.member.ActivityStatus;
import atwoz.atwoz.member.command.domain.member.Grade;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

public record AdminMemberSettingInfo(
    @Schema(implementation = Grade.class)
    String grade,
    boolean isProfilePublic,
    @Schema(implementation = ActivityStatus.class)
    String activityStatus,
    boolean isVip,
    boolean isPushNotificationEnabled
) {
    @QueryProjection
    public AdminMemberSettingInfo {
    }
}
