package atwoz.atwoz.member.application.dto;

import lombok.Builder;

@Builder
public record MemberLoginResponse(
        String accessToken,
        boolean isProfileSettingNeeded
) {
}
