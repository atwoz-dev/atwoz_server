package atwoz.atwoz.member.query.member.view;

import atwoz.atwoz.member.command.domain.member.ActivityStatus;
import atwoz.atwoz.member.command.domain.member.Grade;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

// TODO : 프로필 공개 여부 추가
public record AdminMemberSettingInfo(
    @Schema(implementation = Grade.class)
    String grade,
    @Schema(implementation = ActivityStatus.class)
    String activityStatus,
    boolean isVip,
    boolean isPushNotificationEnabled
) {
    @QueryProjection
    public AdminMemberSettingInfo {
    }
}
