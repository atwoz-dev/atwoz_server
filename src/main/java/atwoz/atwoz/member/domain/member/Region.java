package atwoz.atwoz.member.domain.member;

import lombok.Getter;

@Getter
public enum Region {
    Seoul("Seoul"), Daejeon("Daejeon");

    private final String description;

    Region(String description) {
        this.description = description;
    }
}
