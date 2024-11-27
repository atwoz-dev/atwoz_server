package awtoz.awtoz.common.auth.domain;

public enum Role {
    MEMBER("MEMBER"),
    ADMIN("ADMIN");

    private final String description;

    Role(String description) {
        this.description = description;
    }
}
