package awtoz.awtoz.member.presentation.auth.support;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.Optional;

public class MemberTokenExtractor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";


    public static Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(header)) {
            return Optional.empty();
        }

        else
            return extractTokenFromHeader(header.split(" "));
    }

    private static Optional<String> extractTokenFromHeader(String[] header) {
        if (header.length != 2 || !header[0].equals(BEARER_PREFIX)) {
            return Optional.empty();
        }

        return Optional.of(header[1]);
    }
}
