package atwoz.atwoz.community.command.domain.profileexchange;

import lombok.Getter;

@Getter
public enum ProfileExchangeStatus {
    WAITING("응답 대기중"),
    APPROVE("교환 수락"),
    REJECTED("교환 거절");

    private final String description;

    ProfileExchangeStatus(String description) {
        this.description = description;
    }
}
