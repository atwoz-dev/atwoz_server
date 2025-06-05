package atwoz.atwoz.member.query.member.infra.view;

import com.querydsl.core.annotations.QueryProjection;

public record ProfileAccessView(
    Boolean isIntroduced,
    Long matchRequesterId,
    Long matchResponderId,
    Long profileExchangeRequesterId,
    Long profileExchangeResponderId,
    String profileExchangeStaus,
    Boolean likeReceived

) {
    @QueryProjection
    public ProfileAccessView {
    }
}
