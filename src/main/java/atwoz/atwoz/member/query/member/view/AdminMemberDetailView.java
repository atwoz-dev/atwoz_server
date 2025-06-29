package atwoz.atwoz.member.query.member.view;

import atwoz.atwoz.member.query.member.AgeConverter;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;
import java.util.List;


public record AdminMemberDetailView(
    AdminMemberSettingInfo settingInfo,
    AdminMemberStatusInfo statusInfo,
    BasicInfo basicInfo,
    HeartBalanceView heartBalanceInfo,
    List<String> profileImageUrls,
    ProfileInfo profileInfo,
    List<InterviewInfoView> interviewInfos,
    LocalDateTime createdAt,
    LocalDateTime deletedAt
) {
    @QueryProjection
    public AdminMemberDetailView(AdminMemberSettingInfo adminMemberSettingInfo,
        AdminMemberStatusInfo adminMemberStatusInfo
        , String nickname, String gender, String kakaoId, Integer yearOfBirth, Integer height, String phoneNumber,
        HeartBalanceView heartBalanceInfo, List<String> profileImageUrls, ProfileInfo profileInfo,
        List<InterviewInfoView> interviewInfos, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this(adminMemberSettingInfo, adminMemberStatusInfo,
            new BasicInfo(nickname, gender, kakaoId, AgeConverter.toAge(yearOfBirth), height, phoneNumber),
            heartBalanceInfo, profileImageUrls, profileInfo, interviewInfos, createdAt, deletedAt);
    }
}
