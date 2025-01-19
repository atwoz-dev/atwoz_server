package atwoz.atwoz.member.domain.member;

import lombok.Getter;

@Getter
public enum MemberScreeningStatus {
    PENDING("대기"),
    APPROVED("승인"),
    REJECTED("반려");

    private final String description;

    MemberScreeningStatus(String description) {
        this.description = description;
    }
}
