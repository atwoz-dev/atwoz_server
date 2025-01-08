package atwoz.atwoz.auth.presentation;

import atwoz.atwoz.auth.domain.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AuthContext {

    private Long id;
    private Role role;

    public void authenticate(Long id, Role role) {
        setId(id);
        setRole(role);
    }

    private void setId(Long id) {
        if (id == null) throw new IllegalArgumentException("id는 null일 수 없습니다.");
        this.id = id;
    }

    private void setRole(Role role) {
        if (role == null) throw new IllegalArgumentException("role은 null일 수 없습니다.");
        this.role = role;
    }
}
