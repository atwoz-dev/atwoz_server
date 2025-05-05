package atwoz.atwoz.member.query.member.view;

import com.querydsl.core.annotations.QueryProjection;

import java.util.Set;

public record MemberInfoView(
    StatusInfo statusInfo,
    BasicInfo basicInfo,
    ProfileInfo profileInfo
) {
    @QueryProjection
    public MemberInfoView(String activityStatus, Boolean isVip, String nickname, String gender, String kakaoId,
        Integer yearOfBirth, Integer height, String phoneNumber, String job, String highestEducation, String city,
        String district, String mbti, String smokingStatus, String drinkingStatus, String religion,
        Set<String> hobbies) {
        this(new StatusInfo(activityStatus, isVip),
            new BasicInfo(nickname, gender, kakaoId, yearOfBirth, height, phoneNumber),
            new ProfileInfo(job, highestEducation, city, district, mbti, smokingStatus, drinkingStatus, religion,
                hobbies));
    }
}
