package deepple.deepple.member.query.member.view;

import deepple.deepple.member.command.domain.introduction.IntroductionType;
import io.swagger.v3.oas.annotations.media.Schema;

public record IntroductionInfo(
    @Schema(implementation = IntroductionType.class)
    String introductionType
) {
}
