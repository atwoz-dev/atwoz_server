package atwoz.atwoz.member.presentation.member;

import atwoz.atwoz.member.command.domain.member.*;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
import atwoz.atwoz.member.presentation.member.dto.BasicInfo;
import atwoz.atwoz.member.presentation.member.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.query.member.AgeConverter;
import atwoz.atwoz.member.query.member.view.BasicMemberInfo;

public class MemberMapper {

    public static MemberProfile toMemberProfile(MemberProfileUpdateRequest memberProfileUpdateRequest) {
        return MemberProfile.builder()
                .yearOfBirth(memberProfileUpdateRequest.yearOfBirth())
                .height(memberProfileUpdateRequest.height())
                .jobId(memberProfileUpdateRequest.jobId())
                .hobbyIds(memberProfileUpdateRequest.hobbyIds())
                .nickname(Nickname.from(memberProfileUpdateRequest.nickName()))
                .gender(Gender.from(memberProfileUpdateRequest.gender()))
                .mbti(Mbti.from(memberProfileUpdateRequest.mbti()))
                .region(Region.from(memberProfileUpdateRequest.region()))
                .smokingStatus(SmokingStatus.from(memberProfileUpdateRequest.smokingStatus()))
                .drinkingStatus(DrinkingStatus.from(memberProfileUpdateRequest.drinkingStatus()))
                .religion(Religion.from(memberProfileUpdateRequest.religionStatus()))
                .highestEducation(HighestEducation.from(memberProfileUpdateRequest.highestEducation()))
                .build();
    }

    public static BasicInfo toBasicInfo(BasicMemberInfo basicMemberInfo) {
        return new BasicInfo(basicMemberInfo.id(), basicMemberInfo.nickname(), basicMemberInfo.profileImageUrl(), AgeConverter.toAge(basicMemberInfo.yearOfBirth()), basicMemberInfo.gender(), basicMemberInfo.height(), basicMemberInfo.job(), basicMemberInfo.hobbies(), basicMemberInfo.mbti(), basicMemberInfo.region(), basicMemberInfo.smokingStatus(), basicMemberInfo.drinkingStatus(), basicMemberInfo.highestEducation(), basicMemberInfo.religion(), basicMemberInfo.like());
    }
}
