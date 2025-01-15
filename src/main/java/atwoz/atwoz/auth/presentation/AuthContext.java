package atwoz.atwoz.auth.presentation;

import atwoz.atwoz.common.enums.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AuthContext {

    private long id;
    private Role role;

    public void authenticate(long id, @NonNull Role role) {
        this.id = id;
        this.role = role;
    }
}
