package atwoz.atwoz.member.domain.member;

import atwoz.atwoz.member.exception.InvalidNickNameException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@EqualsAndHashCode
public class Nickname {

    private static final String NICKNAME_REGEX = "^[a-zA-Z0-9가-힣]{1,10}$";

    @Column(name = "name")
    private final String nickname;

    private Nickname(String nickName) {
        validateNickName(nickName);
        this.nickname = nickName;
    }

    private void validateNickName(String nickname) {
        if (nickname == null) {
            throw new InvalidNickNameException();
        }

        if (!nickname.matches(NICKNAME_REGEX)) {
            throw new InvalidNickNameException();
        }
    }

    public static Nickname from(String nickname) {
        return new Nickname(nickname);
    }
}
