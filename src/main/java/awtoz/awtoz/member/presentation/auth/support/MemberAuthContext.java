package awtoz.awtoz.member.presentation.auth.support;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
public class MemberAuthContext {
    private static final Long ANONYMOUS_MEMBER = -1L;

    private Long memberId;

    public void setAuthentication(Long memberId) {
        this.memberId = memberId;
    }

    public Long getPrincipal() {
        if (memberId == null) {
            return ANONYMOUS_MEMBER;
        }
        return memberId;
    }

    public void setAnonymous() {
        this.memberId = ANONYMOUS_MEMBER;
    }
}
