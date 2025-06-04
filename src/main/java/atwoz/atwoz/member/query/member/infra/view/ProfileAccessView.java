package atwoz.atwoz.member.query.member.infra.view;

import com.querydsl.core.annotations.QueryProjection;

public record ProfileAccessView(
    Boolean isIntroduced,
    Long requesterId,
    Long responderId,
    String profileExchangeStaus,
    Boolean likeReceived

) {
    @QueryProjection
    public ProfileAccessView {
    }
}
