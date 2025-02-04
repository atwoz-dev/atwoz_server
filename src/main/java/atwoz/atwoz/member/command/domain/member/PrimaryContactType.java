package atwoz.atwoz.member.command.domain.member;

import lombok.Getter;

@Getter
public enum PrimaryContactType {
    KAKAO("카카오ID"), PHONE_NUMBER("전화번호");

    private String description;

    PrimaryContactType(String description) {
        this.description = description;
    }
}
