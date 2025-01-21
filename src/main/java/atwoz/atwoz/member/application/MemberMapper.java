package atwoz.atwoz.member.application;

import atwoz.atwoz.member.application.dto.MemberContactResponse;
import atwoz.atwoz.member.application.dto.MemberProfileResponse;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateResponse;
import atwoz.atwoz.member.domain.member.*;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MemberMapper {

    public static MemberProfile toMemberProfile(MemberProfileUpdateRequest memberProfileUpdateRequest) {
        return MemberProfile.builder()
                .age(memberProfileUpdateRequest.age())
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

    public static MemberProfileResponse toMemberProfileResponse(Member member) {
        return new MemberProfileResponse(member.getProfile());
    }

    public static MemberContactResponse toMemberContactResponse(Member member) {
        return new MemberContactResponse(member.getPhoneNumber(), member.getKakaoId());
    }
}
