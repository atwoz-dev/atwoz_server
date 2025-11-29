package deepple.deepple.member.query.member.view;

public record ProfileExchangeInfo(
    Long profileExchangeId,
    Long requesterId,
    Long responderId,
    String profileExchangeStatus
) {
}
