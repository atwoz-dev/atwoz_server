package atwoz.atwoz.heart.command.application.heartusagepolicy;

import atwoz.atwoz.heart.command.domain.hearttransaction.HeartTransaction;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionType;
import atwoz.atwoz.member.command.domain.member.Member;

public interface HeartUsageService {
    HeartTransaction useHeart(Member member, TransactionType transactionType);
}