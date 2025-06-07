package atwoz.atwoz.member.query.member.view;

import com.querydsl.core.annotations.QueryProjection;

public record ProfileAccessView(
    boolean isIntroduced,
    Long matchRequesterId,
    Long matchResponderId,
    Long profileExchangeRequesterId,
    Long profileExchangeResponderId,
    String profileExchangeStatus,
    boolean likeReceived

) {
    @QueryProjection
    public ProfileAccessView {
    }
}
