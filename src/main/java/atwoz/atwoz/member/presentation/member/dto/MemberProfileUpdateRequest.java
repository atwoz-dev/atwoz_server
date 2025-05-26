package atwoz.atwoz.member.presentation.member.dto;

import atwoz.atwoz.member.command.domain.member.*;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public record MemberProfileUpdateRequest(
    String nickName,
    @Schema(implementation = Gender.class)
    String gender,
    Integer yearOfBirth,
    Integer height,
    @Schema(implementation = District.class)
    String district,
    @Schema(implementation = HighestEducation.class)
    String highestEducation,
    @Schema(implementation = Mbti.class)
    String mbti,
    @Schema(implementation = SmokingStatus.class)
    String smokingStatus,
    @Schema(implementation = DrinkingStatus.class)
    String drinkingStatus,
    @Schema(implementation = Religion.class)
    String religion,
    @ArraySchema(
        arraySchema = @Schema(implementation = Hobby.class),
        schema = @Schema(implementation = String.class)
    )
    Set<String> hobbies,
    @Schema(implementation = Job.class)
    String job
) {
}
