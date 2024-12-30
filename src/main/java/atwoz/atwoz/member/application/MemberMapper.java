package atwoz.atwoz.member.application;

import atwoz.atwoz.member.application.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.domain.member.*;
import atwoz.atwoz.member.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.domain.member.vo.Nickname;

public class MemberMapper {
    private static MemberProfile toMemberProfile(MemberProfileUpdateRequest memberProfileUpdateRequest) {
        return MemberProfile.builder()
                .age(memberProfileUpdateRequest.age())
                .mbti(Mbti.valueOf(memberProfileUpdateRequest.mbti()))
                .jobId(memberProfileUpdateRequest.jobId())
                .nickname(Nickname.from(memberProfileUpdateRequest.nickName()))
                .region(Region.valueOf(memberProfileUpdateRequest.region()))
                .gender(Gender.valueOf(memberProfileUpdateRequest.gender()))
                .height(memberProfileUpdateRequest.height())
                .lastEducation(LastEducation.valueOf(memberProfileUpdateRequest.lastEducation()))
                .religionStatus(ReligionStatus.valueOf(memberProfileUpdateRequest.religionStatus()))
                .smokingStatus(SmokingStatus.valueOf(memberProfileUpdateRequest.smokingStatus()))
                .drinkingStatus(DrinkingStatus.valueOf(memberProfileUpdateRequest.drinkingStatus()))
                .build();
    }
}
