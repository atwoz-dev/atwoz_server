package atwoz.atwoz.auth.application;

import atwoz.atwoz.auth.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final TokenParser tokenParser;
    private final TokenRepository tokenRepository;

    public AuthResult authenticate(String accessToken, String refreshToken) {
        if (isValid(accessToken)) {
            AuthMember authMember = parseAuthMember(accessToken);
            return AuthResult.success(authMember.getId(), authMember.getRole().toString());
        }

        if (isExpired(accessToken)) {
            if (refreshToken == null) {
                return AuthResult.error("MISSING_REFRESH_TOKEN");
            }

            return reissueTokens(refreshToken);
        }

        return AuthResult.error("INVALID_ACCESS_TOKEN");
    }

    private boolean isValid(String token) {
        return tokenParser.isValid(token);
    }

    private boolean isExpired(String token) {
        return tokenParser.isExpired(token);
    }

    private AuthMember parseAuthMember(String token) {
        long id = tokenParser.getId(token);
        Role role = tokenParser.getRole(token);
        return AuthMember.of(id, role);
    }

    private AuthResult reissueTokens(String refreshToken) {
        if (!isValid(refreshToken) || !exists(refreshToken)) {
            invalidateRefreshToken(refreshToken);
            return AuthResult.error("INVALID_REFRESH_TOKEN");
        }

        AuthMember member = parseAuthMember(refreshToken);

        invalidateRefreshToken(refreshToken);

        String newRefresh = tokenProvider.createRefreshToken(member.getId(), member.getRole(), Instant.now());
        tokenRepository.save(newRefresh);

        String newAccess = tokenProvider.createAccessToken(member.getId(), member.getRole(), Instant.now());

        return AuthResult.reissued(member.getId(), member.getRole().name(), newAccess, newRefresh);
    }

    public boolean exists(String token) {
        return tokenRepository.exists(token);
    }

    private void invalidateRefreshToken(String token) {
        if (token != null) {
            tokenRepository.delete(token);
        }
    }

    public String createAccessToken(long id, Role role, Instant issuedAt) {
        return tokenProvider.createAccessToken(id, role, issuedAt);
    }

    public String createRefreshToken(long id, Role role, Instant issuedAt) {
        String refreshToken = tokenProvider.createRefreshToken(id, role, issuedAt);
        tokenRepository.save(refreshToken);
        return refreshToken;
    }
}
