package atwoz.atwoz.member.query.member.view;

import atwoz.atwoz.member.command.domain.member.Gender;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

public record BasicInfo(
    String nickname,
    @Schema(implementation = Gender.class)
    String gender,
    String kakaoId,
    Integer yearOfBirth,
    Integer height,
    String phoneNumber
) {
    @QueryProjection
    public BasicInfo {
    }
}