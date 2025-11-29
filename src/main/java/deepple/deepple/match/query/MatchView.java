package deepple.deepple.match.query;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record MatchView(
    long matchId,
    long opponentId,
    String opponentMessage,
    String nickName,
    String profileImageUrl,
    String city,
    String myMessage,
    String matchStatus,
    LocalDateTime createdAt
) {
    @QueryProjection
    public MatchView {
    }
}
