package atwoz.atwoz.admin.command.domain.memberscreening;

import lombok.Getter;

@Getter
public enum ScreeningStatus {
    PENDING("대기"),
    APPROVED("승인"),
    REJECTED("반려");

    private final String description;

    ScreeningStatus(String description) {
        this.description = description;
    }
}
