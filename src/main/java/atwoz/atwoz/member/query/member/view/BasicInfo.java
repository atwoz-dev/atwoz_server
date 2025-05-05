package atwoz.atwoz.member.query.member.view;

public record BasicInfo(
    String nickname,
    String gender,
    String kakaoId,
    Integer yearOfBirth,
    Integer height,
    String phoneNumber
) {
}
