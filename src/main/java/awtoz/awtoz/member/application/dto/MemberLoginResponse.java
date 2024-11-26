package awtoz.awtoz.member.application.dto;

import awtoz.awtoz.member.domain.member.Member;
import lombok.Builder;

@Builder
public record MemberLoginResponse(
        String accessToken,
        String refreshToken,
        boolean isNeedProfile
) {
    public static MemberLoginResponse fromMemberWithToken(Member member,String accessToken, String refreshToken) {

        return MemberLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNeedProfile(member.isProfileSettingNeeded())
                .build();
    }
}
