package awtoz.awtoz.member.domain.member;

import lombok.Getter;

@Getter
public enum ActivityStatus {
    ACTIVE("활동중"),
    PERMANENT_STOP("영구정지"),
    TEMPORARY_STOP("일시정지");

    private final String description;

    ActivityStatus(String description) {
        this.description = description;
    }
}
