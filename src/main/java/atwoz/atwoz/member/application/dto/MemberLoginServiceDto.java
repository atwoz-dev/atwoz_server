package atwoz.atwoz.member.application.dto;

import lombok.Builder;

@Builder
public record MemberLoginServiceDto(
        String accessToken,
        String refreshToken,
        boolean isProfileSettingNeeded
) {
    public static MemberLoginServiceDto fromMemberWithToken(String accessToken, String refreshToken, Boolean isProfileSettingNeeded) {

        return MemberLoginServiceDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isProfileSettingNeeded(isProfileSettingNeeded)
                .build();
    }
}
