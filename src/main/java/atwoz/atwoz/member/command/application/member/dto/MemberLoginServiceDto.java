package atwoz.atwoz.member.command.application.member.dto;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
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
