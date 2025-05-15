package atwoz.atwoz.heart.query.hearttransaction.view;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record HeartTransactionView(
    Long id,
    LocalDateTime createdAt,
    String transactionType,
    Long heartAmount,
    Long heartBalance
) {
    @QueryProjection
    public HeartTransactionView {
    }
}
