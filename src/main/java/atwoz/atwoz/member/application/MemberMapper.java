package atwoz.atwoz.member.application;

import atwoz.atwoz.member.application.dto.MemberLoginResponse;
import atwoz.atwoz.member.application.dto.MemberLoginServiceDto;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.application.dto.MemberProfileUpdateResponse;
import atwoz.atwoz.member.domain.member.*;
import atwoz.atwoz.member.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.domain.member.vo.Nickname;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemberMapper {
    public static MemberProfile toMemberProfile(Long memberId, MemberProfileUpdateRequest memberProfileUpdateRequest) {

        List<MemberHobby> memberHobbyList = memberProfileUpdateRequest.hobbyIds() == null ? new ArrayList<>()
                : memberProfileUpdateRequest.hobbyIds().stream().map(
                (hobbyId) -> MemberHobby.of(memberId, hobbyId)
        ).collect(Collectors.toList());

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

    public static MemberLoginResponse toMemberLoginResponse(MemberLoginServiceDto dto) {
        return MemberLoginResponse.builder()
                .accessToken(dto.accessToken())
                .isProfileSettingNeeded(dto.isProfileSettingNeeded())
                .build();
    }
}
