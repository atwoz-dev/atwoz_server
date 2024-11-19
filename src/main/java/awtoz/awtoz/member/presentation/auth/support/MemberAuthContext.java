package awtoz.awtoz.member.presentation.auth.support;

import awtoz.awtoz.member.exception.auth.TokenNotExistException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
public class MemberAuthContext {

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
