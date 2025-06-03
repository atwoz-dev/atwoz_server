package atwoz.atwoz.member.query.introduction.application;

import atwoz.atwoz.like.command.domain.LikeLevel;
import atwoz.atwoz.member.command.domain.member.Hobby;
import atwoz.atwoz.member.command.domain.member.Mbti;
import atwoz.atwoz.member.command.domain.member.Religion;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MemberIntroductionProfileView(
    long memberId,
    String profileImageUrl,
    @ArraySchema(schema = @Schema(implementation = Hobby.class))
    List<String> hobbies,
    @Schema(implementation = Mbti.class)
    String mbti,
    @Schema(implementation = Religion.class)
    String religion,
    String interviewAnswerContent,
    @Schema(implementation = LikeLevel.class)
    String likeLevel,
    boolean isIntroduced
) {
}
