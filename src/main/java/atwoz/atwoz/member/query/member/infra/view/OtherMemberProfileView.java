package atwoz.atwoz.member.query.member.infra.view;

import com.querydsl.core.annotations.QueryProjection;

import java.util.Set;

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
        Set<String> hobbies,
        String mbti,
        String city,
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
        this(new BasicMemberInfo(id, nickname, profileImageUrl, yearOfBirth, gender, height, job, hobbies, mbti, city,
                smokingStatus, drinkingStatus, highestEducation, religion, like),
            matchId == null ? null
                : new MatchInfo(matchId, requesterId, responderId, requestMessage, responseMessage, matchStatus,
                    contactType, contact));
    }
}
