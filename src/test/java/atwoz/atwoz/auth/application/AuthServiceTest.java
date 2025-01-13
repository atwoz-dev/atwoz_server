package atwoz.atwoz.auth.application;

import atwoz.atwoz.auth.domain.TokenParser;
import atwoz.atwoz.auth.domain.TokenProvider;
import atwoz.atwoz.auth.domain.TokenRepository;
import atwoz.atwoz.common.enums.Role;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String VALID_ACCESS_TOKEN = "validAccessToken";
    private static final String INVALID_ACCESS_TOKEN = "invalidAccessToken";
    private static final String EXPIRED_ACCESS_TOKEN = "expiredAccessToken";
    private static final String REISSUED_ACCESS_TOKEN = "reissuedAccessToken";

    private static final String VALID_REFRESH_TOKEN = "validRefreshToken";
    private static final String INVALID_REFRESH_TOKEN = "invalidRefreshToken";
    private static final String EXPIRED_REFRESH_TOKEN = "expiredRefreshToken";
    private static final String REISSUED_REFRESH_TOKEN = "reissuedRefreshToken";

    private final long MEMBER_ID = 1234L;
    private final Role MEMBER_ROLE = Role.MEMBER;
    private final Instant NOW = Instant.now();

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private TokenParser tokenParser;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private AuthService authService;

    // AT null인 경우
    // AT 유효한 경우
    // AR 유효하지 않은 경우

    // AT 만료된 경우
        // RT null인 경우
        // RT 만료된 경우
        // RT 유효하지 않은 경우
        // RT 존재하지 않는 경우
        // RT 유효한 경우
}
