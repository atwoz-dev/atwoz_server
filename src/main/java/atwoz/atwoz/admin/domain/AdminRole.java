package atwoz.atwoz.admin.domain;

import lombok.Getter;

@Getter
public enum AdminRole {
    GENERAL("일반 관리자"),
    MASTER("마스터 관리자");

    private final String description;

    AdminRole(String description) {
        this.description = description;
    }
}