package atwoz.atwoz.auth.application;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AuthResult {

    private final boolean success;
    private final Long memberId;
    private final String role;
    private final String reissuedAccessToken;
    private final String reissuedRefreshToken;
    private final String errorCode;

    public static AuthResult success(long memberId, String role) {
        return new AuthResult(true, memberId, role, null, null, null);
    }

    public static AuthResult reissued(long memberId, String role, String accessToken, String refreshToken) {
        return new AuthResult(true, memberId, role, accessToken, refreshToken, null);
    }

    public static AuthResult error(String errorCode) {
        return new AuthResult(false, null, null, null, null, errorCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isReissued() {
        return (reissuedAccessToken != null && reissuedRefreshToken != null);
    }
}
