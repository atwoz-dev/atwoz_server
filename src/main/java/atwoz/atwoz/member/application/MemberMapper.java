package atwoz.atwoz.member.application;

import atwoz.atwoz.member.application.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateResponse;
import atwoz.atwoz.member.domain.member.*;
import lombok.NoArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MemberMapper {

    public static MemberProfile toMemberProfile(Long memberId, MemberProfileUpdateRequest memberProfileUpdateRequest) {

        List<MemberHobby> memberHobbyList = memberProfileUpdateRequest.hobbyIds() == null ? List.of() :
                memberProfileUpdateRequest.hobbyIds().stream()
                        .map(hobbyId -> MemberHobby.of(memberId, hobbyId))
                        .toList();

        return MemberProfile.builder()
                .age(memberProfileUpdateRequest.age())
                .mbti(Mbti.from(memberProfileUpdateRequest.mbti()))
                .jobId(memberProfileUpdateRequest.jobId())
                .nickname(Nickname.from(memberProfileUpdateRequest.nickName()))
                .region(Region.from(memberProfileUpdateRequest.region()))
                .gender(Gender.from(memberProfileUpdateRequest.gender()))
                .height(memberProfileUpdateRequest.height())
                .highestEducation(HighestEducation.from(memberProfileUpdateRequest.highestEducation()))
                .religionStatus(ReligionStatus.from(memberProfileUpdateRequest.religionStatus()))
                .smokingStatus(SmokingStatus.from(memberProfileUpdateRequest.smokingStatus()))
                .drinkingStatus(DrinkingStatus.from(memberProfileUpdateRequest.drinkingStatus()))
                .memberHobbyList(memberHobbyList)
                .build();
    }

    public static MemberProfileUpdateResponse toMemberProfileUpdateResponse(Member member) {
        return new MemberProfileUpdateResponse(member.getProfile());
    }
}
