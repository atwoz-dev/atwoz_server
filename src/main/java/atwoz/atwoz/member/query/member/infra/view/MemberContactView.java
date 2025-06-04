package atwoz.atwoz.member.query.member.infra.view;

import atwoz.atwoz.member.command.domain.member.PrimaryContactType;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberContactView(
    String phoneNumber,
    String kakaoId,
    @Schema(implementation = PrimaryContactType.class)
    String primaryContactType
) {
    @QueryProjection
    public MemberContactView {
    }
}
