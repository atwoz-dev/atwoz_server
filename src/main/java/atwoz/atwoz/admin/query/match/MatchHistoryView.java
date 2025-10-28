package atwoz.atwoz.admin.query.match;

import atwoz.atwoz.match.command.domain.match.MatchStatus;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

public record MatchHistoryView(
    long matchId,
    String requesterNickname,
    String responderNickname,
    String requestMessage,
    String responseMessage,
    @Schema(implementation = MatchStatus.class)
    String status,
    String readByResponderAt
) {
    @QueryProjection
    public MatchHistoryView {
    }
}