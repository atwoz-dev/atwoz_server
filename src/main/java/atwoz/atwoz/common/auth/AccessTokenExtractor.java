package atwoz.atwoz.common.auth;

import atwoz.atwoz.common.auth.exception.MissingAuthorizationHeaderException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessTokenExtractor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public static Optional<String> extractFrom(HttpServletRequest request) {
        String authorizationHeader = getAuthorizationHeaderFrom(request)
                .orElseThrow(MissingAuthorizationHeaderException::new);

        return parseAccessTokenFrom(authorizationHeader);
    }

    private static Optional<String> getAuthorizationHeaderFrom(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER));
    }

    private static Optional<String> parseAccessTokenFrom(String header) {
        if (header.startsWith(BEARER_PREFIX)) {
            return Optional.of(header.substring(BEARER_PREFIX.length()).trim());
        }
        return Optional.empty();
    }
}
