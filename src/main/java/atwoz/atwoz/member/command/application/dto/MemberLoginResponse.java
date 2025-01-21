package atwoz.atwoz.member.command.application.dto;

import lombok.Builder;

@Builder
public record MemberLoginResponse(
        String accessToken,
        boolean isProfileSettingNeeded
) {
}
