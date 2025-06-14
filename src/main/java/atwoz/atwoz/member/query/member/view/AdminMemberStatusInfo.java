package atwoz.atwoz.member.query.member.view;

import atwoz.atwoz.member.command.domain.member.PrimaryContactType;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

// TODO : 모의고사 응시 여부, 지인차단 여부 추가
public record AdminMemberStatusInfo(
    @Schema(implementation = PrimaryContactType.class)
    String primaryContactType,
    boolean hasInterviewAnswer,
    int warningCount
) {
    @QueryProjection
    public AdminMemberStatusInfo {
    }
}
