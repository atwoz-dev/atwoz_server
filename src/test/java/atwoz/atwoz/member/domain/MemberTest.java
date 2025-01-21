package atwoz.atwoz.member.domain;

import atwoz.atwoz.hearttransaction.domain.vo.HeartAmount;
import atwoz.atwoz.hearttransaction.domain.vo.HeartBalance;
import atwoz.atwoz.member.domain.member.KakaoId;
import atwoz.atwoz.member.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MemberTest {

    @Test
    @DisplayName("유효한 전화번호를 사용하여 멤버를 생성합니다.")
    void createMemberWithValidValueType() {
        // Given
        String phoneNumber = "01012345678";

        // When
        Member member = Member.fromPhoneNumber(phoneNumber);

        // Then
        Assertions.assertThat(member).isNotNull();
        Assertions.assertThat(member.isProfileSettingNeeded()).isTrue();
        Assertions.assertThat(member.isBanned()).isFalse();
    }

    @Nested
    @DisplayName("Member 의 상태 변화 메서드 테스트")
    class MemberStatusChangeTest {

        @Test
        @DisplayName("멤버의 활동 상태를 휴면 상태로 전환합니다.")
        void changeMemberActivityStatusToDormant() {
            // Given
            Member member = Member.fromPhoneNumber("01012345678");

            // When
            member.transitionToDormant();

            // Then
            Assertions.assertThat(member.isActive()).isFalse();
        }

        @Test
        @DisplayName("멤버의 카카오 아이디를 변경합니다.")
        void changeMemberKakaoId() {
            // Given
            Member member = Member.fromPhoneNumber("01012345678");
            String kakaoId = "kongtae";

            // When
            member.updateKakaoId(KakaoId.from(kakaoId));

            // Then
            Assertions.assertThat(member.getKakaoId()).isEqualTo(kakaoId);
        }

        @Test
        @DisplayName("멤버의 휴대폰 번호를 변경합니다.")
        void changeMemberPhoneNumber() {
            // Given
            Member member = Member.fromPhoneNumber("01012345678");
            String phoneNumber = "01087564321";

            // When
            member.updatePhoneNumber(phoneNumber);

            // Then
            Assertions.assertThat(member.getPhoneNumber()).isEqualTo(phoneNumber);
        }
    }

    @Nested
    @DisplayName("gainPurchaseHeart 메서드 테스트")
    class GainPurchaseHeartMethodTest {
        @Test
        @DisplayName("멤버가 하트를 구매하면 구매 하트 잔액이 증가합니다.")
        void shouldIncreasePurchaseHeartBalanceWhenMemberPurchasesHeart() {
            // Given
            Member member = Member.fromPhoneNumber("01012345678");
            HeartAmount purchaseHeartAmount = HeartAmount.from(100L);
            HeartBalance expectedHeartBalance = HeartBalance.init().gainPurchaseHeart(purchaseHeartAmount);

            // When
            member.gainPurchaseHeart(purchaseHeartAmount);

            // Then
            Assertions.assertThat(member.getHeartBalance()).isEqualTo(expectedHeartBalance);
        }
    }

    @Nested
    @DisplayName("useHeart 메서드 테스트")
    class UseHeartMethodTest {
        @Test
        @DisplayName("멤버가 하트를 사용하면 하트 잔액이 차감됩니다.")
        void shouldDeductHeartBalanceWhenMemberUsesHeart() {
            // Given
            Member member = Member.fromPhoneNumber("01012345678");
            member.gainPurchaseHeart(HeartAmount.from(100L));
            HeartAmount usingheartAmount = HeartAmount.from(-10L);
            HeartAmount expectedHeartAmount = HeartAmount.from(90L);
            HeartBalance expectedHeartBalance = HeartBalance.init().gainPurchaseHeart(expectedHeartAmount);

            // When
            member.useHeart(usingheartAmount);

            // Then
            Assertions.assertThat(member.getHeartBalance()).isEqualTo(expectedHeartBalance);
        }
    }

    @Nested
    @DisplayName("gainMissionHeart 메서드 테스트")
    class GainMissionHeartMethodTest {
        @Test
        @DisplayName("멤버가 미션을 수행하면 미션 하트 잔액이 증가합니다.")
        void shouldIncreaseMissionHeartBalanceWhenMemberCompletesMission() {
            // Given
            Member member = Member.fromPhoneNumber("01012345678");
            HeartAmount missionHeartAmount = HeartAmount.from(100L);
            HeartBalance expectedHeartBalance = HeartBalance.init().gainMissionHeart(missionHeartAmount);

            // When
            member.gainMissionHeart(missionHeartAmount);

            // Then
            Assertions.assertThat(member.getHeartBalance()).isEqualTo(expectedHeartBalance);
        }
    }
}
