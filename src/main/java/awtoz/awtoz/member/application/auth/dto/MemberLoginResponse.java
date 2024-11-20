package awtoz.awtoz.member.application.auth.dto;

import awtoz.awtoz.member.domain.member.Member;
import lombok.Builder;

@Builder
public record MemberLoginResponse(
        String accessToken,
        String refreshToken,
        boolean isSuspended,
        boolean isNeedProfile
) {
    public static MemberLoginResponse fromMemberWithToken(Member member,String accessToken, String refreshToken) {

        return MemberLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isSuspended(member.isPermanentStop())
                .isNeedProfile(member.isNeedProfile())
                .build();
    }
}
