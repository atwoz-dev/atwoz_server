package atwoz.atwoz.member.query.member.view;

public record MatchInfo(
        Long matchId,
        Long requesterId,
        Long responderId,
        String requestMessage,
        String responseMessage,
        String matchStatus,
        String contactType,
        String contact
) {
}
