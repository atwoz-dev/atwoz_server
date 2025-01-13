package atwoz.atwoz.member.domain.member.vo;

import atwoz.atwoz.member.exception.InvalidKakaoIdException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoId {

    private static final String KAKAO_ID_REGEX = "^[a-z0-9._-]{3,15}$";
    private static final Pattern KAKAO_ID_PATTERN = Pattern.compile(KAKAO_ID_REGEX);


    private String id;

    public static KakaoId from(String id) {
        return new KakaoId(id);
    }

    private KakaoId(@NonNull String id) {
        if (!KAKAO_ID_PATTERN.matcher(id).matches()) {
            throw new InvalidKakaoIdException();
        }
        this.id = id;
    }
}
