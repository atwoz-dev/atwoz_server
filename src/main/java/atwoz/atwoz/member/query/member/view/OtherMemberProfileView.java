package atwoz.atwoz.member.query.member.view;

import com.querydsl.core.annotations.QueryProjection;

import java.util.List;

public record OtherMemberProfileView(
        Long id,
        BasicMemberInfo basicMemberInfo,
        MatchInfo matchInfo
) {
    @QueryProjection
    public OtherMemberProfileView(
            Long id, String nickname, String profileImageUrl,
            Integer age,
            String gender,
            Integer height,
            String job,
            List<String> hobbies,
            String mbti,
            String region,
            String smokingStatus,
            String drinkingStatus,
            String highestEducation,
            String religion,
            Long matchId,
            Long requesterId,
            Long responderId,
            String requestMessage,
            String responseMessage,
            String matchStatus,
            String contactType,
            String contact
    ) {
        this(id, new BasicMemberInfo(nickname, profileImageUrl, age, gender, height, job, hobbies, mbti, region, smokingStatus, drinkingStatus, highestEducation, religion),
                new MatchInfo(matchId, requesterId, responderId, requestMessage, responseMessage, matchStatus, contactType, contact));
    }
}
