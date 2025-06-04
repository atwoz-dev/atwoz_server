package atwoz.atwoz.member.query.member.infra.view;

import com.querydsl.core.annotations.QueryProjection;

public record HeartBalanceView(
    long purchaseHeartBalance,
    long missionHeartBalance,
    long totalHeartBalance
) {
    @QueryProjection
    public HeartBalanceView {
    }
}
