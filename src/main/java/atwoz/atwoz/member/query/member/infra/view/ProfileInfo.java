package atwoz.atwoz.member.query.member.infra.view;

import atwoz.atwoz.member.command.domain.member.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public record ProfileInfo(
    @Schema(implementation = Job.class)
    String job,
    @Schema(implementation = HighestEducation.class)
    String highestEducation,
    @Schema(implementation = City.class)
    String city,
    @Schema(implementation = District.class)
    String district,
    @Schema(implementation = Mbti.class)
    String mbti,
    @Schema(implementation = SmokingStatus.class)
    String smokingStatus,
    @Schema(implementation = DrinkingStatus.class)
    String drinkingStatus,
    @Schema(implementation = Religion.class)
    String religion,
    @Schema(implementation = Hobby.class)
    Set<String> hobbies
) {
}
