package atwoz.atwoz.heart.command.application.heartusagepolicy;

import atwoz.atwoz.heart.command.domain.hearttransaction.HeartTransaction;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionType;

public interface HeartUsageService {
    HeartTransaction useHeart(long memberId, TransactionType transactionType);
}