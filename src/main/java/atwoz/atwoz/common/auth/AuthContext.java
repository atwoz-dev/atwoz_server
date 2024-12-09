package atwoz.atwoz.common.auth;

import atwoz.atwoz.common.auth.exception.TokenNotExistException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class AuthContext {

    private Long id;
    private Role role;

    public void setAuthentication(Long memberId, Role role) {
        this.id = memberId;
        this.role = role;
    }

    public Long getPrincipal() {
        if (id == null) {
            throw new TokenNotExistException();
        }
        return id;
    }
}
