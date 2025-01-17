package atwoz.atwoz.member.domain.member.vo;

import atwoz.atwoz.member.exception.InvalidNickNameException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@EqualsAndHashCode
public class Nickname {

    private static final String NICKNAME_REGEX = "^[a-zA-Z0-9가-힣]{1,10}$";

    @Column(name = "name")
    private final String value;

    public static Nickname from(String nickname) {
        return new Nickname(nickname);
    }

    private Nickname(@NonNull String value) {
        if (!value.matches(NICKNAME_REGEX)) {
            throw new InvalidNickNameException();
        }
        this.value = value;
    }
}
