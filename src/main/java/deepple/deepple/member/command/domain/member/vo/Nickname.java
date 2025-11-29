package deepple.deepple.member.command.domain.member.vo;

import deepple.deepple.member.command.domain.member.exception.InvalidNicknameException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@EqualsAndHashCode
public class Nickname {

    private static final String NICKNAME_REGEX = "^[a-zA-Z0-9가-힣]{1,10}$";

    @Column(name = "nickname")
    private final String value;

    private Nickname(@NonNull String value) {
        if (!value.matches(NICKNAME_REGEX)) {
            throw new InvalidNicknameException();
        }
        this.value = value;
    }

    public static Nickname from(String nickname) {
        return new Nickname(nickname);
    }
}
