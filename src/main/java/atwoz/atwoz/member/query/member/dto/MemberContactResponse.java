package atwoz.atwoz.member.query.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class MemberContactResponse {
    private String phoneNumber;
    private String kakaoId;
    private String primaryContactType;

    @QueryProjection
    public MemberContactResponse(String phoneNumber, String kakaoId, String primaryContactType) {
        this.phoneNumber = phoneNumber;
        this.kakaoId = kakaoId;
        this.primaryContactType = primaryContactType;
    }
}
