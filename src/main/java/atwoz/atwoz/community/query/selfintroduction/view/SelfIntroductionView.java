package atwoz.atwoz.community.query.selfintroduction.view;

import com.querydsl.core.annotations.QueryProjection;

import java.util.List;

public record SelfIntroductionView(
        MemberBasicInfo memberBasicInfo,
        String like,
        String title,
        String content
) {
    @QueryProjection
    public SelfIntroductionView(Long memberId,
                                String nickname,
                                Integer yearOfBirth,
                                String profileImageUrl,
                                String city,
                                String district,
                                String mbti,
                                List<String> hobbies, String like, String title, String content) {
        this(new MemberBasicInfo(memberId, nickname, yearOfBirth, profileImageUrl, city, district, mbti, hobbies), like, title, content);
    }
}
