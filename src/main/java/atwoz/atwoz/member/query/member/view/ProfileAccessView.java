package atwoz.atwoz.member.query.member.view;

import com.querydsl.core.annotations.QueryProjection;

public record ProfileAccessView(
    Boolean isIntroduced,
    Boolean isExchanged
) {
    @QueryProjection
    public ProfileAccessView {
    }
}
