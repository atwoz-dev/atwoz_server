package atwoz.atwoz.member.presentation.member.dto;

import atwoz.atwoz.member.command.domain.member.ActivityStatus;
import atwoz.atwoz.member.command.domain.member.Grade;
import io.swagger.v3.oas.annotations.media.Schema;

public record AdminMemberSettingUpdateRequest(
    @Schema(implementation = Grade.class)
    String grade,
    @Schema(implementation = ActivityStatus.class)
    String activityStatus,
    boolean isVip,
    boolean isPushNotificationEnabled
) {
}
