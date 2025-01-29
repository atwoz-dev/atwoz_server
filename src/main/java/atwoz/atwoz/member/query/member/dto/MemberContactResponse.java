package atwoz.atwoz.member.query.member.dto;

public record MemberContactResponse(
        String phoneNumber,
        String kakaoId,
        String primaryContactType
) {
}
