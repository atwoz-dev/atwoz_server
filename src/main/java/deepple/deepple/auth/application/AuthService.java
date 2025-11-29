package deepple.deepple.auth.application;

import deepple.deepple.auth.domain.TokenParser;
import deepple.deepple.auth.domain.TokenProvider;
import deepple.deepple.auth.domain.TokenRepository;
import deepple.deepple.common.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static deepple.deepple.auth.application.AuthErrorStatus.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final TokenParser tokenParser;
    private final TokenRepository tokenRepository;

    public AuthResponse authenticate(String accessToken, String refreshToken) {
        if (accessToken == null) {
            return AuthResponse.error(MISSING_ACCESS_TOKEN);
        }

        if (isValid(accessToken)) {
            return AuthResponse.authenticated(getId(accessToken), getRole(accessToken));
        }

        if (isExpired(accessToken)) {
            if (refreshToken == null) {
                return AuthResponse.error(MISSING_REFRESH_TOKEN);
            }

            if (isExpired(refreshToken)) {
                invalidateRefreshToken(refreshToken);
                return AuthResponse.error(EXPIRED_REFRESH_TOKEN);
            }

            if (!isValid(refreshToken) || !exists(refreshToken)) {
                return AuthResponse.error(INVALID_REFRESH_TOKEN);
            }

            return reissueTokens(refreshToken);
        }

        return AuthResponse.error(INVALID_ACCESS_TOKEN);
    }

    private AuthResponse reissueTokens(String refreshToken) {
        invalidateRefreshToken(refreshToken);

        long id = getId(refreshToken);
        Role role = getRole(refreshToken);
        Instant issuedAt = Instant.now();

        String reissuedAccessToken = createAccessToken(id, role, issuedAt);
        String reissuedRefreshToken = createRefreshToken(id, role, issuedAt);

        return AuthResponse.reissued(id, role, reissuedAccessToken, reissuedRefreshToken);
    }

    private Role getRole(String token) {
        return tokenParser.getRole(token);
    }

    private long getId(String token) {
        return tokenParser.getId(token);
    }

    private boolean isValid(String token) {
        return tokenParser.isValid(token);
    }

    private boolean isExpired(String token) {
        return tokenParser.isExpired(token);
    }

    private boolean exists(String token) {
        return tokenRepository.exists(token);
    }

    private void invalidateRefreshToken(String token) {
        tokenRepository.delete(token);
    }

    private String createAccessToken(long id, Role role, Instant issuedAt) {
        return tokenProvider.createAccessToken(id, role, issuedAt);
    }

    private String createRefreshToken(long id, Role role, Instant issuedAt) {
        String refreshToken = tokenProvider.createRefreshToken(id, role, issuedAt);
        tokenRepository.save(refreshToken);
        return refreshToken;
    }
}
