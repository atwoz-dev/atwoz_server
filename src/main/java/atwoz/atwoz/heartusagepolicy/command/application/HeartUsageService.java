package atwoz.atwoz.heartusagepolicy.command.application;

import atwoz.atwoz.hearttransaction.command.domain.HeartTransaction;
import atwoz.atwoz.hearttransaction.command.domain.vo.TransactionType;
import atwoz.atwoz.member.command.domain.member.Member;

public interface HeartUsageService {
    HeartTransaction useHeart(Member member, TransactionType transactionType);
}