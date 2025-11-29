package deepple.deepple.member.query.member.view;

import com.querydsl.core.annotations.QueryProjection;
import deepple.deepple.member.command.domain.member.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

public record BasicInfo(
    String nickname,
    @Schema(implementation = Gender.class)
    String gender,
    String kakaoId,
    Integer age,
    Integer height,
    String phoneNumber
) {
    @QueryProjection
    public BasicInfo {
    }
}