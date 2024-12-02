package atwoz.atwoz.common.auth;

import atwoz.atwoz.common.auth.exception.InvalidAuthorizationHeaderException;
import atwoz.atwoz.common.auth.exception.MissingAuthorizationHeaderException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccessTokenExtractor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public String extract(HttpServletRequest request) {
        String authorizationHeader = getAuthorizationHeader(request)
                .orElseThrow(MissingAuthorizationHeaderException::new);

        return parseAccessToken(authorizationHeader)
                .orElseThrow(InvalidAuthorizationHeaderException::new);
    }

    private Optional<String> getAuthorizationHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER));
    }

    private Optional<String> parseAccessToken(String header) {
        if (header.startsWith(BEARER_PREFIX)) {
            return Optional.of(header.substring(BEARER_PREFIX.length()).trim());
        }
        return Optional.empty();
    }
}
