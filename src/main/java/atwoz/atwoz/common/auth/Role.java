package atwoz.atwoz.common.auth;

import lombok.Getter;

public enum Role {
    MEMBER("회원"),
    ADMIN("관리자");

    @Getter
    private final String description;

    Role(String description) {
        this.description = description;
    }
}
