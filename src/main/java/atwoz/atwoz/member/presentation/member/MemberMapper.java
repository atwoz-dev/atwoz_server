package atwoz.atwoz.member.presentation.member;

import atwoz.atwoz.match.command.domain.match.MatchContactType;
import atwoz.atwoz.match.command.domain.match.MatchStatus;
import atwoz.atwoz.member.command.domain.member.*;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
import atwoz.atwoz.member.command.domain.member.vo.Region;
import atwoz.atwoz.member.presentation.member.dto.ContactInfo;
import atwoz.atwoz.member.presentation.member.dto.MemberInfo;
import atwoz.atwoz.member.presentation.member.dto.MemberMyProfileResponse;
import atwoz.atwoz.member.presentation.member.dto.MemberProfileUpdateRequest;
import atwoz.atwoz.member.query.member.AgeConverter;
import atwoz.atwoz.member.query.member.view.BasicMemberInfo;
import atwoz.atwoz.member.query.member.view.ContactView;
import atwoz.atwoz.member.query.member.view.MatchInfo;
import atwoz.atwoz.member.query.member.view.MemberProfileView;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberMapper {

    public static MemberProfile toMemberProfile(MemberProfileUpdateRequest memberProfileUpdateRequest) {
        return MemberProfile.builder()
            .yearOfBirth(memberProfileUpdateRequest.yearOfBirth())
            .height(memberProfileUpdateRequest.height())
            .nickname(Nickname.from(memberProfileUpdateRequest.nickname()))
            .gender(Gender.from(memberProfileUpdateRequest.gender()))
            .mbti(Mbti.from(memberProfileUpdateRequest.mbti()))
            .region(Region.of(District.from(memberProfileUpdateRequest.district())))
            .smokingStatus(SmokingStatus.from(memberProfileUpdateRequest.smokingStatus()))
            .drinkingStatus(DrinkingStatus.from(memberProfileUpdateRequest.drinkingStatus()))
            .religion(Religion.from(memberProfileUpdateRequest.religion()))
            .highestEducation(HighestEducation.from(memberProfileUpdateRequest.highestEducation()))
            .job(Job.from(memberProfileUpdateRequest.job()))
            .hobbies(memberProfileUpdateRequest.hobbies() != null ? memberProfileUpdateRequest.hobbies()
                .stream()
                .map(Hobby::from)
                .collect(Collectors.toSet()) : null)
            .build();
    }

    public static MemberInfo toBasicInfo(BasicMemberInfo basicMemberInfo) {
        return new MemberInfo(basicMemberInfo.id(), basicMemberInfo.nickname(), basicMemberInfo.profileImageUrl(),
            AgeConverter.toAge(basicMemberInfo.yearOfBirth()), basicMemberInfo.gender(), basicMemberInfo.height(),
            basicMemberInfo.job(), basicMemberInfo.hobbies(), basicMemberInfo.mbti(), basicMemberInfo.city(),
            basicMemberInfo.district(),
            basicMemberInfo.smokingStatus(), basicMemberInfo.drinkingStatus(), basicMemberInfo.highestEducation(),
            basicMemberInfo.religion(), basicMemberInfo.like());
    }

    public static MemberMyProfileResponse toMemberMyProfileResponse(MemberProfileView view) {
        return new MemberMyProfileResponse(view.nickname(), AgeConverter.toAge(view.yearOfBirth()), view.gender(),
            view.height(),
            view.job(), view.hobbies(), view.mbti(), view.city(), view.district(), view.smokingStatus(),
            view.drinkingStatus(), view.highestEducation(), view.religion());
    }

    public static ContactInfo toContactInfo(ContactView contactView, MatchInfo matchInfo, Long targetMemberId) {
        if (contactView == null || matchInfo == null) {
            return null;
        }
        if (matchInfo.matchStatus() == null || MatchStatus.valueOf(matchInfo.matchStatus()) != MatchStatus.MATCHED) {
            return null;
        }
        String contactType = targetMemberId.equals(matchInfo.requesterId()) ? matchInfo.requesterContactType()
            : matchInfo.responderContactType();
        String contact = switch (MatchContactType.valueOf(contactType)) {
            case PHONE_NUMBER -> contactView.phoneNumber();
            case KAKAO -> contactView.kakaoId();
        };
        return new ContactInfo(contactType, contact);
    }
}
