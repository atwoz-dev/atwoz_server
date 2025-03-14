package atwoz.atwoz.member.query.introduction.application;

import lombok.Getter;

@Getter
public enum IntroductionCacheKeyPrefix {
    DIAMOND("diamond:"),
    SAME_HOBBY("hobby:"),
    SAME_RELIGION("religion:"),
    SAME_REGION("region:"),
    RECENTLY_JOINED("recentlyJoined:");

    private final String prefix;

    IntroductionCacheKeyPrefix(String prefix) {
        this.prefix = prefix;
    }
}
