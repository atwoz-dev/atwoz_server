package atwoz.atwoz.member.query.member.infra.view;

public record ProfileExchangeInfo(
    Long profileExchangeId,
    Long requesterId,
    Long responderId,
    String profileExchangeStatus
) {
}
