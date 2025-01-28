package atwoz.atwoz.member.application.dto;

public record MemberContactResponse(
        String phoneNumber,
        String kakaoId,
        String primaryContactType
) {
}
