package atwoz.atwoz.member.application.dto;

import atwoz.atwoz.member.domain.member.Member;
import lombok.Builder;

@Builder
public record MemberLoginServiceDto(
        String accessToken,
        String refreshToken,
        boolean isProfileSettingNeeded
) {
    public static MemberLoginServiceDto fromMemberWithToken(Member member,String accessToken, String refreshToken) {

        return MemberLoginServiceDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isProfileSettingNeeded(member.isProfileSettingNeeded())
                .build();
    }
}
