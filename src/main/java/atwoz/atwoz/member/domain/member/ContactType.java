package atwoz.atwoz.member.domain.member;

import lombok.Getter;

@Getter
public enum ContactType {
    KAKAO("카카오ID"), PHONE_NUMBER("전화번호");

    private String description;

    ContactType(String description) {
        this.description = description;
    }
}
