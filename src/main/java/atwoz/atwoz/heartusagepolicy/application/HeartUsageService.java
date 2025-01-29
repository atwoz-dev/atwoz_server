package atwoz.atwoz.heartusagepolicy.application;

import atwoz.atwoz.hearttransaction.domain.HeartTransaction;
import atwoz.atwoz.hearttransaction.domain.vo.TransactionType;
import atwoz.atwoz.member.command.domain.member.Member;

public interface HeartUsageService {
    HeartTransaction useHeart(Member member, TransactionType transactionType);
}