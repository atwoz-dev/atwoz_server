package atwoz.atwoz.member.presentation.member.dto;

public record MemberLoginResponse(
        String accessToken,
        boolean isProfileSettingNeeded
) {
}
