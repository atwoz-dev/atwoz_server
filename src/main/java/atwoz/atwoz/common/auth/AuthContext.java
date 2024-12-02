package atwoz.atwoz.common.auth;

import atwoz.atwoz.common.auth.exception.TokenNotExistException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
public class AuthContext {

    private Long memberId;

    public void setAuthentication(Long memberId) {
        this.memberId = memberId;
    }

    public Long getPrincipal() {
        if (memberId == null) {
            throw new TokenNotExistException();
        }
        return memberId;
    }
}
