package atwoz.atwoz.member.command.application.member.dto;

public record MemberLoginServiceDto(
    String accessToken,
    String refreshToken,
    boolean isProfileSettingNeeded
) {
}
