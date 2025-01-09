package atwoz.atwoz.auth.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class AuthMember {

    private final long id;
    private final Role role;

    public static AuthMember of(long id, Role role) {
        return new AuthMember(id, role);
    }
}
