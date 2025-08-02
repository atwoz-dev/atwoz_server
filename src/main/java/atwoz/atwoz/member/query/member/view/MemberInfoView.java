package atwoz.atwoz.member.query.member.view;

import atwoz.atwoz.member.query.member.AgeConverter;
import com.querydsl.core.annotations.QueryProjection;

import java.util.Set;

public record MemberInfoView(
    StatusInfo statusInfo,
    BasicInfo basicInfo,
    ProfileInfo profileInfo,
    Set<InterviewInfoView> interviewInfoView
) {
    @QueryProjection
    public MemberInfoView(Long memberId, String activityStatus, Boolean isVip, String contactType, String nickname,
        String gender,
        String kakaoId,
        Integer yearOfBirth, Integer height, String phoneNumber, String job, String highestEducation, String city,
        String district, String mbti, String smokingStatus, String drinkingStatus, String religion,
        Set<String> hobbies, Set<InterviewInfoView> interviewInfoView) {
        this(new StatusInfo(memberId, activityStatus, isVip, contactType),
            new BasicInfo(nickname, gender, kakaoId, AgeConverter.toAge(yearOfBirth), height, phoneNumber),
            new ProfileInfo(job, highestEducation, city, district, mbti, smokingStatus, drinkingStatus, religion,
                hobbies), interviewInfoView);
    }
}
