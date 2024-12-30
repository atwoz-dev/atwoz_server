package atwoz.atwoz.member.domain.member;

import lombok.Getter;

@Getter
public enum ReligionStatus {
    ;
    private String description;
    ReligionStatus(String description) {
        this.description = description;
    }
}
