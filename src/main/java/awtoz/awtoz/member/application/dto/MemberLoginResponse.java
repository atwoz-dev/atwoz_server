package awtoz.awtoz.member.application.dto;

import awtoz.awtoz.member.domain.member.Member;
import lombok.Builder;

@Builder
public record MemberLoginResponse(
        String accessToken,
        String refreshToken,
        boolean isProfileSettingNeeded
) {
    public static MemberLoginResponse fromMemberWithToken(Member member,String accessToken, String refreshToken) {

        return MemberLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isProfileSettingNeeded(member.isProfileSettingNeeded())
                .build();
    }
}
