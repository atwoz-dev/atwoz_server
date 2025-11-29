package deepple.deepple.admin.query.match;

import com.querydsl.core.annotations.QueryProjection;
import deepple.deepple.match.command.domain.match.MatchStatus;
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