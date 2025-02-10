package atwoz.atwoz.heartusagepolicy.command.application.heartusagepolicy;

import atwoz.atwoz.hearttransaction.command.domain.hearttransaction.HeartTransaction;
import atwoz.atwoz.hearttransaction.command.domain.hearttransaction.vo.TransactionType;
import atwoz.atwoz.member.command.domain.member.Member;

public interface HeartUsageService {
    HeartTransaction useHeart(Member member, TransactionType transactionType);
}