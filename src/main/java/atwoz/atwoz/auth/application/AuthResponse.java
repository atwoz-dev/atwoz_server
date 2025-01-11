package atwoz.atwoz.auth.application;

import atwoz.atwoz.common.enums.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AuthResponse {

    private final AuthStatus status;
    private final Long memberId;
    private final Role role;
    private final String accessToken;
    private final String refreshToken;
    private final AuthErrorStatus errorStatus;

    public static AuthResponse authenticated(long memberId, Role role) {
        return new AuthResponse(AuthStatus.AUTHENTICATED, memberId, role, null, null, null);
    }

    public static AuthResponse reissued(long memberId, Role role, String accessToken, String refreshToken) {
        return new AuthResponse(AuthStatus.REISSUED, memberId, role, accessToken, refreshToken, null);
    }

    public static AuthResponse error(AuthErrorStatus errorStatus) {
        return new AuthResponse(AuthStatus.ERROR, null, null, null, null, errorStatus);
    }

    public boolean isAuthenticated() {
        return status == AuthStatus.AUTHENTICATED;
    }

    public boolean isReissued() {
        return status == AuthStatus.REISSUED;
    }

    public boolean isError() {
        return status == AuthStatus.ERROR;
    }

    private enum AuthStatus {
        AUTHENTICATED, REISSUED, ERROR
    }
}
