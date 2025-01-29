package atwoz.atwoz.member.command.application.member;

import atwoz.atwoz.member.query.member.dto.MemberContactResponse;
import atwoz.atwoz.member.command.application.member.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.command.domain.member.*;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
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

    public static MemberContactResponse toMemberContactResponse(Member member) {
        return new MemberContactResponse(member.getPhoneNumber(), member.getKakaoId(), member.getPrimaryContactType().toString());
    }
}
