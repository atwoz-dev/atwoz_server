package atwoz.atwoz.heartusagepolicy.domain;

import atwoz.atwoz.hearttransaction.domain.vo.TransactionType;
import atwoz.atwoz.heartusagepolicy.exception.InvalidUsageTypeException;

public class UsageTypeMapper {
    public static TransactionType toTransactionType(UsageType usageType) {
        if (usageType == null) {
            throw new IllegalArgumentException("UsageType must not be null");
        }
        switch (usageType) {
            case MESSAGE:
                return TransactionType.MESSAGE;
            case DATING_EXAM:
                return TransactionType.DATING_EXAM;
            case PROFILE_EXCHANGE:
                return TransactionType.PROFILE_EXCHANGE;
            case SELF_INTRODUCTION:
                return TransactionType.SELF_INTRODUCTION;
            default:
                throw new InvalidUsageTypeException("Invalid UsageType: " + usageType);
        }
    }
}