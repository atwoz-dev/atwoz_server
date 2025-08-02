package atwoz.atwoz.member.query.member.view;

import atwoz.atwoz.member.command.domain.member.ActivityStatus;
import atwoz.atwoz.member.command.domain.member.PrimaryContactType;
import io.swagger.v3.oas.annotations.media.Schema;

// TODO : 푸시알림 설정 여부, 인터뷰 작성 여부, 모의고사 응시 여부, 지인차단 여부.
public record StatusInfo(
    Long memberId,
    @Schema(implementation = ActivityStatus.class)
    String activityStatus,
    Boolean isVip,
    @Schema(implementation = PrimaryContactType.class)
    String primaryContactType
) {
}
