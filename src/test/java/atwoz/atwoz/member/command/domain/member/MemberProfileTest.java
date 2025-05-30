package atwoz.atwoz.member.command.domain.member;

import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
import atwoz.atwoz.member.command.domain.member.vo.Region;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Set;

class MemberProfileTest {
    @DisplayName("값이 하나라도 Null 이 있다면, 프로필 업데이트가 필요한 대상이다.")
    @Test
    void isNeededProfileSettingWhenNullValueExists() {
        // Given
        MemberProfile memberProfile = MemberProfile.builder()
            .yearOfBirth(Calendar.getInstance().get(Calendar.YEAR) - 25) // 26살
            .height(180)
            .job(Job.JOB_SEARCHING)
            .mbti(Mbti.ENFJ)
            .drinkingStatus(DrinkingStatus.NONE)
            .smokingStatus(SmokingStatus.VAPE)
            .religion(Religion.NONE)
            .highestEducation(HighestEducation.DOCTORATE)
            .gender(Gender.MALE)
            .nickname(Nickname.from("Hello"))
            .hobbies(Set.of(Hobby.ANIMATION))
            .region(null)
            .build();

        // When & Then
        Assertions.assertThat(memberProfile.isProfileSettingNeeded()).isTrue();
    }

    @DisplayName("취미 목록이 비어있는 경우에, 프로필 업데이트가 필요한 대상이다.")
    @Test
    void isNeededProfileSettingWhenHobbyListIsEmpty() {
        // Given
        MemberProfile memberProfile = MemberProfile.builder()
            .yearOfBirth(Calendar.getInstance().get(Calendar.YEAR) - 25) // 26살
            .height(180)
            .job(Job.JOB_SEARCHING)
            .mbti(Mbti.ENFJ)
            .drinkingStatus(DrinkingStatus.NONE)
            .smokingStatus(SmokingStatus.VAPE)
            .religion(Religion.NONE)
            .highestEducation(HighestEducation.DOCTORATE)
            .gender(Gender.MALE)
            .nickname(Nickname.from("Hello"))
            .hobbies(Set.of())
            .region(Region.of(District.GANGBUK_GU))
            .build();

        // When & Then
        Assertions.assertThat(memberProfile.isProfileSettingNeeded()).isTrue();
    }

    @DisplayName("취미 목록이 비어있지 않고, 모든 값이 Null 이 아닌 경우, 프로필 업데이트가 필요하지 않은 대상이다.")
    @Test
    void isNeededProfileSettingWhenHobbyListIsNotEmptyAndNullValueDoesntNotExist() {
        // Given
        MemberProfile memberProfile = MemberProfile.builder()
            .yearOfBirth(Calendar.getInstance().get(Calendar.YEAR) - 25) // 26살
            .height(180)
            .job(Job.JOB_SEARCHING)
            .mbti(Mbti.ENFJ)
            .drinkingStatus(DrinkingStatus.NONE)
            .smokingStatus(SmokingStatus.VAPE)
            .religion(Religion.NONE)
            .highestEducation(HighestEducation.DOCTORATE)
            .gender(Gender.MALE)
            .nickname(Nickname.from("Hello"))
            .hobbies(Set.of(Hobby.ANIMATION))
            .region(Region.of(District.GANGBUK_GU))
            .build();

        // When & Then
        Assertions.assertThat(memberProfile.isProfileSettingNeeded()).isFalse();
    }

}
