package atwoz.atwoz.member.query.member.view;

import atwoz.atwoz.member.query.member.AgeConverter;
import com.querydsl.core.annotations.QueryProjection;

import java.util.List;

public record OtherMemberProfileView(
        BasicMemberInfo basicMemberInfo,
        MatchInfo matchInfo
) {
    @QueryProjection
    public OtherMemberProfileView(
            Long id, String nickname, String profileImageUrl,
            Integer yearOfBirth,
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
            String like,
            Long matchId,
            Long requesterId,
            Long responderId,
            String requestMessage,
            String responseMessage,
            String matchStatus,
            String contactType,
            String contact
    ) {
        this(new BasicMemberInfo(id, nickname, profileImageUrl, AgeConverter.toAge(yearOfBirth), gender, height, job, hobbies, mbti, region, smokingStatus, drinkingStatus, highestEducation, religion, like),
                new MatchInfo(matchId, requesterId, responderId, requestMessage, responseMessage, matchStatus, contactType, contact));
    }
}
