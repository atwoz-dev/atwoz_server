package atwoz.atwoz.member.command.application.member.dto;

public record MemberLoginResponse(
        String accessToken,
        boolean isProfileSettingNeeded
) {
}
