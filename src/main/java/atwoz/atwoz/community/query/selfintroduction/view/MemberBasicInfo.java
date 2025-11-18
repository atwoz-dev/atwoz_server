package atwoz.atwoz.community.query.selfintroduction.view;

import atwoz.atwoz.member.command.domain.member.City;
import atwoz.atwoz.member.command.domain.member.District;
import atwoz.atwoz.member.command.domain.member.Hobby;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public record MemberBasicInfo(
    Long memberId,
    String nickname,
    Integer age,
    String profileImageUrl,
    @Schema(implementation = City.class)
    String city,
    @Schema(implementation = District.class)
    String district,
    @Schema(implementation = String.class)
    String mbti,
    @ArraySchema(schema = @Schema(implementation = Hobby.class))
    Set<String> hobbies
) {
}
