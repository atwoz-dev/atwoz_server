package atwoz.atwoz.member.query.member.view;

import atwoz.atwoz.member.query.member.AgeConverter;
import com.querydsl.core.annotations.QueryProjection;

import java.util.List;
import java.util.Set;

public record OtherMemberProfileView(
        BasicMemberInfo basicMemberInfo,
        MatchInfo matchInfo
) {
    /**
     * Constructs an {@code OtherMemberProfileView} from individual member and match attributes.
     *
     * Creates a profile view by aggregating basic member information and, if available, match-related details.
     *
     * @param hobbies the set of hobbies associated with the member
     */
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
        this(new BasicMemberInfo(id, nickname, profileImageUrl, yearOfBirth, gender, height, job, hobbies, mbti, city, smokingStatus, drinkingStatus, highestEducation, religion, like),
                matchId == null ? null : new MatchInfo(matchId, requesterId, responderId, requestMessage, responseMessage, matchStatus, contactType, contact));
    }
}
