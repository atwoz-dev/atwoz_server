package deepple.deepple.member.query.member.view;

import com.querydsl.core.annotations.QueryProjection;
import deepple.deepple.member.command.domain.member.ActivityStatus;
import deepple.deepple.member.command.domain.member.Grade;
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
