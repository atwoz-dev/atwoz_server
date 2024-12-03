package atwoz.atwoz.common.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AccessToken {

    private final String value;

    public static AccessToken from(String value) {
        return new AccessToken(value);
    }

    public Long getId() {
        return null;
    }

    public Role getRole() {
        return null;
    }
}
