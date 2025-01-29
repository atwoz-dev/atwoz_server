package atwoz.atwoz.member.command.application.member.dto;

import lombok.Builder;

@Builder
public record MemberLoginResponse(
        String accessToken,
        boolean isProfileSettingNeeded
) {
}
