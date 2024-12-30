package atwoz.atwoz.member.domain.member;

import lombok.Getter;

@Getter
public enum SmokingStatus {
    ;

    private String description;

    SmokingStatus(String description) {
        this.description = description;
    }
}
