package atwoz.atwoz.member.command.domain.member.vo;

import atwoz.atwoz.member.command.domain.member.exception.InvalidKakaoIdException;
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
public class KakaoId {

    private static final String KAKAO_ID_REGEX = "^[a-zA-Z0-9._-]{3,15}$";

    @Column(name = "kakaoId")
    private String value;

    private KakaoId(String value) {
        if (!value.matches(KAKAO_ID_REGEX)) {
            throw new InvalidKakaoIdException();
        }
        this.value = value;
    }

    public static KakaoId from(String kakaoId) {
        return new KakaoId(kakaoId);
    }
}
