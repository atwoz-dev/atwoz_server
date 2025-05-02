package atwoz.atwoz.heart.command.domain.hearttransaction.vo;


public enum TransactionType {
    MISSION,
    MESSAGE,
    DATING_EXAM,
    PROFILE_EXCHANGE,
    SELF_INTRODUCTION,
    INTRODUCTION,
    PURCHASE;

    public boolean isUsingType() {
        return this == MESSAGE || this == DATING_EXAM || this == PROFILE_EXCHANGE || this == SELF_INTRODUCTION ||
            this == INTRODUCTION;
    }

    public boolean isGainingType() {
        return this == MISSION || this == PURCHASE;
    }
}