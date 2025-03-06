package atwoz.atwoz.notification.domain;

import lombok.Getter;

@Getter
public enum SenderType {
    ADMIN("관리자"),
    MEMBER("회원");

    private final String description;

    SenderType(String description) {
        this.description = description;
    }
}
