package awtoz.awtoz.member.domain.member;

import awtoz.awtoz.member.exception.InvalidNickNameException;
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
public class NickName {

    @Column(name = "name")
    private final String nickName;

    private NickName(String nickName) {
        this.nickName = nickName;
    }

    private void validateNickName(String nickName) {
        if (nickName == null) {
            throw new InvalidNickNameException();
        }
    }

    public static NickName from(String nickName) {
        return new NickName(nickName);
    }
}
