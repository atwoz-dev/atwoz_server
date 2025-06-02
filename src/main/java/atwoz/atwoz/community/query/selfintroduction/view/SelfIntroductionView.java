package atwoz.atwoz.community.query.selfintroduction.view;

import com.querydsl.core.annotations.QueryProjection;

import java.util.Set;

public record SelfIntroductionView(
    MemberBasicInfo memberBasicInfo,
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
        String profileExchangeStatus) {
        this(new MemberBasicInfo(memberId, nickname, yearOfBirth, profileImageUrl, city, district, mbti, hobbies), like,
            title, content, profileExchangeStatus);
    }
}
