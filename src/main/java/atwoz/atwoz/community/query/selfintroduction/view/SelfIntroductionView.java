package atwoz.atwoz.community.query.selfintroduction.view;

import atwoz.atwoz.like.command.domain.LikeLevel;
import atwoz.atwoz.member.query.member.AgeConverter;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public record SelfIntroductionView(
    MemberBasicInfo memberBasicInfo,
    @Schema(implementation = LikeLevel.class)
    String like,
    String title,
    String content,
    String profileExchangeStatus
) {
    @QueryProjection
    public SelfIntroductionView(Long memberId,
        String nickname,
        Integer yearOfBirth,
        String profileImageUrl,
        String city,
        String district,
        String mbti,
        Set<String> hobbies,
        String like,
        String title,
        String content,
        String profileExchangeStatus
    ) {
        this(new MemberBasicInfo(memberId, nickname, AgeConverter.toAge(yearOfBirth), profileImageUrl, city, district,
            mbti, hobbies), like, title, content, profileExchangeStatus);
    }
}
