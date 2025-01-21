package atwoz.atwoz.member.command.application;

import atwoz.atwoz.member.command.application.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.command.application.dto.MemberProfileUpdateResponse;
import atwoz.atwoz.member.command.domain.member.*;
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

    public static MemberProfileUpdateResponse toMemberProfileUpdateResponse(Member member) {
        return new MemberProfileUpdateResponse(member.getProfile());
    }
}
