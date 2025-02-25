package atwoz.atwoz.match.command.domain.match;

import lombok.Getter;

@Getter
public enum MatchStatus {
    WAITING("응답 대기중"),
    MATCHED("매칭 완료."),
    EXPIRED("만료됨"),
    REJECTED("거절");

    private final String description;

    MatchStatus(String description) {
        this.description = description;
    }
}
