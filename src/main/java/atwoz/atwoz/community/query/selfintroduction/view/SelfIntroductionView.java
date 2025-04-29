package atwoz.atwoz.community.query.selfintroduction.view;

import com.querydsl.core.annotations.QueryProjection;

import java.util.List;
import java.util.Set;

public record SelfIntroductionView(
        MemberBasicInfo memberBasicInfo,
        String like,
        String title,
        String content
) {
    /**
     * Constructs a SelfIntroductionView from detailed member information and self-introduction fields.
     *
     * @param memberId the unique identifier of the member
     * @param nickname the member's nickname
     * @param yearOfBirth the member's year of birth
     * @param profileImageUrl the URL of the member's profile image
     * @param city the member's city
     * @param district the member's district
     * @param mbti the member's MBTI type
     * @param hobbies the set of the member's hobbies
     * @param like the like field of the self-introduction
     * @param title the title of the self-introduction
     * @param content the content of the self-introduction
     */
    @QueryProjection
    public SelfIntroductionView(Long memberId,
                                String nickname,
                                Integer yearOfBirth,
                                String profileImageUrl,
                                String city,
                                String district,
                                String mbti,
                                Set<String> hobbies, String like, String title, String content) {
        this(new MemberBasicInfo(memberId, nickname, yearOfBirth, profileImageUrl, city, district, mbti, hobbies), like, title, content);
    }
}
