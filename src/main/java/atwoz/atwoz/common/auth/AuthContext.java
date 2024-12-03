package atwoz.atwoz.common.auth;

import atwoz.atwoz.common.auth.exception.TokenNotExistException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
public class AuthContext {

    private Long memberId;
    private Role role;

    public void setAuthentication(Long memberId, Role role) {
        this.memberId = memberId;
        this.role = role;
    }

    public Long getPrincipal() {
        if (memberId == null) {
            throw new TokenNotExistException();
        }
        return memberId;
    }
}
