package atwoz.atwoz.heart.query.hearttransaction.view;

import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionType;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record HeartTransactionView(
    Long id,
    LocalDateTime createdAt,
    @Schema(implementation = TransactionType.class)
    String transactionType,
    String content,
    Long heartAmount,
    Long heartBalance
) {
    @QueryProjection
    public HeartTransactionView {
    }
}
