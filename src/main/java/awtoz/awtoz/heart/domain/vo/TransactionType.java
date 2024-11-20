package awtoz.awtoz.heart.domain.vo;


public enum TransactionType {
    MISSION,
    MESSAGE,
    DATING_EXAM,
    PROFILE_EXCHANGE,
    INTRODUCTION,
    PURCHASE;

    public boolean isUsingType() {
        return this == MESSAGE || this == DATING_EXAM || this == PROFILE_EXCHANGE || this == INTRODUCTION;
    }

    public boolean isGainingType() {
        return this == MISSION || this == PURCHASE;
    }
}