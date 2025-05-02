package atwoz.atwoz.member.query.member;

import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartAmount;
import atwoz.atwoz.interview.command.domain.answer.InterviewAnswer;
import atwoz.atwoz.interview.command.domain.question.InterviewCategory;
import atwoz.atwoz.interview.command.domain.question.InterviewQuestion;
import atwoz.atwoz.like.command.domain.like.Like;
import atwoz.atwoz.like.command.domain.like.LikeLevel;
import atwoz.atwoz.match.command.domain.match.Match;
import atwoz.atwoz.match.command.domain.match.MatchStatus;
import atwoz.atwoz.match.command.domain.match.vo.Message;
import atwoz.atwoz.member.command.domain.member.*;
import atwoz.atwoz.member.command.domain.member.vo.KakaoId;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
import atwoz.atwoz.member.command.domain.member.vo.Region;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import atwoz.atwoz.member.query.member.view.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Calendar;
import java.util.List;
import java.util.Set;


@DataJpaTest
@Import({QuerydslConfig.class, MemberQueryRepository.class})
public class MemberQueryRepositoryTest {

    @Autowired
    private MemberQueryRepository memberQueryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("프로필 조회 테스트")
    class ProfileQueryTest {

        @Test
        @DisplayName("존재하지 않은 아이디인 경우, 프로필 빈 값 반환.")
        void isNullWhenMemberIsNotExists() {
            // Given
            Long notExistMemberId = 10L;

            // When & Then
            Assertions.assertThat(memberQueryRepository.findContactsByMemberId(notExistMemberId).orElse(null)).isNull();
        }

        @Test
        @DisplayName("존재하는 아이디인 경우, 프로필 조회 성공.")
        void isSuccessWhenMemberIsExists() {
            // Given
            Job job = Job.JOB_SEARCHING;
            Hobby hobby1 = Hobby.ANIMATION;
            Hobby hobby2 = Hobby.BOARD_GAMES;

            Member member = Member.fromPhoneNumber("01012345678");
            MemberProfile updateProfile = MemberProfile.builder()
                .yearOfBirth(Calendar.getInstance().get(Calendar.YEAR) - 25) // 26살
                .height(20)
                .highestEducation(HighestEducation.ASSOCIATE)
                .nickname(Nickname.from("nickname"))
                .region(Region.of(District.DONG_GU_DAEJEON))
                .gender(Gender.MALE)
                .smokingStatus(SmokingStatus.DAILY)
                .mbti(Mbti.ENFJ)
                .drinkingStatus(DrinkingStatus.NONE)
                .religion(Religion.BUDDHIST)
                .job(job)
                .hobbies(Set.of(hobby1, hobby2))
                .build();

            member.updateProfile(updateProfile);
            entityManager.persist(member);
            entityManager.flush();

            // When
            MemberProfileView memberProfileView = memberQueryRepository.findProfileByMemberId(member.getId())
                .orElse(null);

            // Then
            MemberProfile savedMemberProfile = member.getProfile();
            Assertions.assertThat(memberProfileView).isNotNull();
            Assertions.assertThat(memberProfileView.yearOfBirth())
                .isEqualTo(savedMemberProfile.getYearOfBirth().getValue());
            Assertions.assertThat(memberProfileView.height()).isEqualTo(savedMemberProfile.getHeight());
            Assertions.assertThat(memberProfileView.drinkingStatus())
                .isEqualTo(savedMemberProfile.getDrinkingStatus().toString());
            Assertions.assertThat(memberProfileView.job()).isEqualTo(job.name());
            Assertions.assertThat(memberProfileView.hobbies().size()).isEqualTo(savedMemberProfile.getHobbies().size());
            Assertions.assertThat(memberProfileView.nickname()).isEqualTo(savedMemberProfile.getNickname().getValue());
            Assertions.assertThat(memberProfileView.city())
                .isEqualTo(savedMemberProfile.getRegion().getCity().toString());
            Assertions.assertThat(memberProfileView.district())
                .isEqualTo(savedMemberProfile.getRegion().getDistrict().toString());
            Assertions.assertThat(memberProfileView.gender()).isEqualTo(savedMemberProfile.getGender().toString());
            Assertions.assertThat(memberProfileView.smokingStatus())
                .isEqualTo(savedMemberProfile.getSmokingStatus().toString());
            Assertions.assertThat(memberProfileView.mbti()).isEqualTo(savedMemberProfile.getMbti().toString());
            Assertions.assertThat(memberProfileView.drinkingStatus())
                .isEqualTo(savedMemberProfile.getDrinkingStatus().toString());
            Assertions.assertThat(memberProfileView.religion()).isEqualTo(savedMemberProfile.getReligion().toString());
        }
    }

    @Nested
    @DisplayName("연락처 조회 테스트")
    class ContactQueryTest {

        @Test
        @DisplayName("존재하지 않은 아이디의 경우 연락처 조회 실패.")
        void isFailWhenMemberIsNotExists() {
            // Given
            Long notExistMemberId = 10L;

            // When & Then
            Assertions.assertThat(memberQueryRepository.findContactsByMemberId(notExistMemberId).orElse(null)).isNull();
        }


        @Test
        @DisplayName("아이디가 존재하는 경우 연락처 조회 성공.")
        void isSuccessWhenMemberIsExists() {
            // Given
            Member member = Member.fromPhoneNumber("01012345678");
            member.changePrimaryContactTypeToKakao(KakaoId.from("kakaoId"));
            entityManager.persist(member);
            entityManager.flush();

            Long existMemberId = member.getId();

            // When
            MemberContactView memberContactView = memberQueryRepository.findContactsByMemberId(existMemberId)
                .orElse(null);

            // Then
            Assertions.assertThat(memberContactView).isNotNull();
            Assertions.assertThat(memberContactView.phoneNumber()).isEqualTo(member.getPhoneNumber());
            Assertions.assertThat(memberContactView.kakaoId()).isEqualTo(member.getKakaoId());
            Assertions.assertThat(memberContactView.primaryContactType())
                .isEqualTo(member.getPrimaryContactType().toString());
        }
    }

    @Nested
    @DisplayName("다른 유저의 프로필 조회")
    class OtherMemberProfile {
        static MockedStatic<Events> mockedEvents;
        Member member;
        Member otherMember;
        LikeLevel likeLevel = LikeLevel.INTEREST;
        String profileImageUrl = "primaryImage";
        Job jobName = Job.JOB_SEARCHING;
        Set<Hobby> hobbies = Set.of(Hobby.ANIMATION, Hobby.BOARD_GAMES);

        @BeforeEach
        void setUp() {
            mockedEvents = Mockito.mockStatic(Events.class);
            mockedEvents.when(() -> Events.raise(Mockito.any()))
                .thenAnswer(invocation -> null);

            Job job = Job.JOB_SEARCHING;
            Hobby hobby1 = Hobby.ANIMATION;
            Hobby hobby2 = Hobby.BOARD_GAMES;


            otherMember = Member.fromPhoneNumber("01012345678");

            MemberProfile updateProfile = MemberProfile.builder()
                .yearOfBirth(Calendar.getInstance().get(Calendar.YEAR) - 25) // 26살
                .height(20)
                .highestEducation(HighestEducation.ASSOCIATE)
                .nickname(Nickname.from("nickname"))
                .region(Region.of(District.DONG_GU_DAEJEON))
                .gender(Gender.MALE)
                .smokingStatus(SmokingStatus.DAILY)
                .mbti(Mbti.ENFJ)
                .drinkingStatus(DrinkingStatus.NONE)
                .religion(Religion.BUDDHIST)
                .job(job)
                .hobbies(Set.of(hobby1, hobby2))
                .build();

            otherMember.updateProfile(updateProfile);
            entityManager.persist(otherMember);
            entityManager.flush();


            member = Member.fromPhoneNumber("01012345679");
            entityManager.persist(member);
            entityManager.flush();

            // 좋아요 생성.
            Like like = Like.of(member.getId(), otherMember.getId(), likeLevel);
            entityManager.persist(like);
            entityManager.flush();

            // 프로필 이미지.
            ProfileImage profileImage1 = ProfileImage.builder()
                .memberId(otherMember.getId())
                .order(1)
                .imageUrl(ImageUrl.from(profileImageUrl))
                .isPrimary(true)
                .build();

            ProfileImage profileImage2 = ProfileImage.builder()
                .memberId(otherMember.getId())
                .order(2)
                .imageUrl(ImageUrl.from("secondaryImage"))
                .isPrimary(false)
                .build();

            entityManager.persist(profileImage1);
            entityManager.persist(profileImage2);
            entityManager.flush();
        }

        @AfterEach
        void tearDown() {
            mockedEvents.close();
        }

        @Test
        @DisplayName("존재하지 않은 아이디인 경우, 빈 값 반환")
        void getNullWhenMemberIdIsNotExists() {
            // Given
            Long otherMemberId = -1L;

            // When
            OtherMemberProfileView memberProfileView = memberQueryRepository.findOtherProfileByMemberId(member.getId(),
                otherMemberId).orElse(null);

            // Then
            Assertions.assertThat(memberProfileView).isNull();
        }

        @Test
        @DisplayName("상대방과의 매치가 존재하지 않은 경우, 기본 정보만 조회.")
        void getBasicInfoWhenMatchIsNotExists() {
            // When
            OtherMemberProfileView memberProfileView = memberQueryRepository.findOtherProfileByMemberId(member.getId(),
                    otherMember.getId())
                .orElse(null);

            BasicMemberInfo basicMemberInfo = memberProfileView.basicMemberInfo();
            MemberProfile otherMemberProfile = otherMember.getProfile();

            // Then
            Assertions.assertThat(memberProfileView).isNotNull();
            Assertions.assertThat(memberProfileView.basicMemberInfo().id()).isEqualTo(otherMember.getId());

            // BasicInfo.
            assertionsBasicInfo(basicMemberInfo, otherMemberProfile);

            // MatchInfo
            Assertions.assertThat(memberProfileView.matchInfo()).isNull();
        }

        @Test
        @DisplayName("상대방과의 매치가 만료된 경우, 기본 정보만 조회")
        void getBasicInfoWhenExpiredMatchExists() {
            // Given
            Match match = Match.request(member.getId(), otherMember.getId(), Message.from("매치 신청합니다."));
            match.expire();
            entityManager.persist(match);
            entityManager.flush();

            // When
            OtherMemberProfileView memberProfileView = memberQueryRepository.findOtherProfileByMemberId(member.getId(),
                    otherMember.getId())
                .orElse(null);

            BasicMemberInfo basicMemberInfo = memberProfileView.basicMemberInfo();
            MemberProfile otherMemberProfile = otherMember.getProfile();

            // Then
            Assertions.assertThat(memberProfileView).isNotNull();
            Assertions.assertThat(memberProfileView.basicMemberInfo().id()).isEqualTo(otherMember.getId());

            // BasicInfo.
            assertionsBasicInfo(basicMemberInfo, otherMemberProfile);

            // MatchInfo
            Assertions.assertThat(memberProfileView.matchInfo()).isNull();
        }

        @Test
        @DisplayName("상대방과의 매치를 거절 확인한 경우, 기본 정보만 조회")
        void getBasicInfoWhenRejectCheckedMatchExists() {
            // Given
            Match match = Match.request(member.getId(), otherMember.getId(), Message.from("매치 신청합니다."));
            match.reject();
            match.checkRejected();
            entityManager.persist(match);
            entityManager.flush();


            // When
            OtherMemberProfileView memberProfileView = memberQueryRepository.findOtherProfileByMemberId(member.getId(),
                    otherMember.getId())
                .orElse(null);

            BasicMemberInfo basicMemberInfo = memberProfileView.basicMemberInfo();
            MemberProfile otherMemberProfile = otherMember.getProfile();

            // Then
            Assertions.assertThat(memberProfileView).isNotNull();
            Assertions.assertThat(memberProfileView.basicMemberInfo().id()).isEqualTo(otherMember.getId());

            // BasicInfo.
            assertionsBasicInfo(basicMemberInfo, otherMemberProfile);

            // MatchInfo
            Assertions.assertThat(memberProfileView.matchInfo()).isNull();
        }

        @Test
        @DisplayName("상대방에게 매치를 요청한 경우, 기본 정보와 연락처를 제외한 매치 정보를 함께 조회.")
        void getBasicInfoWithMatchInfoNotIncludingContactWhenWaitingMatchExists() {
            // Given
            Match match = Match.request(member.getId(), otherMember.getId(), Message.from("매치 신청합니다."));
            entityManager.persist(match);
            entityManager.flush();


            // When
            OtherMemberProfileView memberProfileView = memberQueryRepository.findOtherProfileByMemberId(member.getId(),
                    otherMember.getId())
                .orElse(null);

            BasicMemberInfo basicMemberInfo = memberProfileView.basicMemberInfo();
            MemberProfile otherMemberProfile = otherMember.getProfile();

            // Then
            Assertions.assertThat(memberProfileView).isNotNull();
            Assertions.assertThat(memberProfileView.basicMemberInfo().id()).isEqualTo(otherMember.getId());

            // BasicInfo.
            assertionsBasicInfo(basicMemberInfo, otherMemberProfile);

            // MatchInfo
            assertionsMatchInfo(memberProfileView.matchInfo(), match);
            Assertions.assertThat(memberProfileView.matchInfo().requesterId()).isEqualTo(member.getId());
        }

        @Test
        @DisplayName("상대방이 매치를 수락한 경우, 기본 정보와 연락처를 포함한 매치 정보를 조회.")
        void getBasicInfoWithMatchInfoIncludingContactWhenWaitingMatchNotExists() {
            // Given
            Match match = Match.request(member.getId(), otherMember.getId(), Message.from("매치 신청합니다."));
            match.approve(Message.from("매치 수락합니다!"));
            entityManager.persist(match);
            entityManager.flush();


            // When
            OtherMemberProfileView memberProfileView = memberQueryRepository.findOtherProfileByMemberId(member.getId(),
                    otherMember.getId())
                .orElse(null);

            BasicMemberInfo basicMemberInfo = memberProfileView.basicMemberInfo();
            MemberProfile otherMemberProfile = otherMember.getProfile();

            // Then
            Assertions.assertThat(memberProfileView).isNotNull();
            Assertions.assertThat(memberProfileView.basicMemberInfo().id()).isEqualTo(otherMember.getId());

            // BasicInfo.
            assertionsBasicInfo(basicMemberInfo, otherMemberProfile);

            // MatchInfo
            assertionsMatchInfo(memberProfileView.matchInfo(), match);
            Assertions.assertThat(memberProfileView.matchInfo().requesterId()).isEqualTo(member.getId());
        }

        @Test
        @DisplayName("상대방이 매치를 거절한 경우, 기본 정보와 연락처를 제외한 매치 정보를 함께 조회.")
        void getBasicInfoWithMatchInfoNotIncludingContactWhenWaitingMatchNotExists() {
            // Given
            Match match = Match.request(member.getId(), otherMember.getId(), Message.from("매치 신청합니다."));
            match.reject();
            entityManager.persist(match);
            entityManager.flush();


            // When
            OtherMemberProfileView memberProfileView = memberQueryRepository.findOtherProfileByMemberId(member.getId(),
                    otherMember.getId())
                .orElse(null);

            BasicMemberInfo basicMemberInfo = memberProfileView.basicMemberInfo();
            MemberProfile otherMemberProfile = otherMember.getProfile();

            // Then
            Assertions.assertThat(memberProfileView).isNotNull();
            Assertions.assertThat(memberProfileView.basicMemberInfo().id()).isEqualTo(otherMember.getId());

            // BasicInfo.
            assertionsBasicInfo(basicMemberInfo, otherMemberProfile);

            // MatchInfo
            assertionsMatchInfo(memberProfileView.matchInfo(), match);
            Assertions.assertThat(memberProfileView.matchInfo().requesterId()).isEqualTo(member.getId());
        }

        private void assertionsBasicInfo(BasicMemberInfo basicMemberInfo, MemberProfile otherMemberProfile) {
            Assertions.assertThat(basicMemberInfo.nickname()).isEqualTo(otherMemberProfile.getNickname().getValue());
            Assertions.assertThat(basicMemberInfo.profileImageUrl()).isEqualTo(profileImageUrl);
            Assertions.assertThat(basicMemberInfo.yearOfBirth())
                .isEqualTo(otherMemberProfile.getYearOfBirth().getValue());
            Assertions.assertThat(basicMemberInfo.gender()).isEqualTo(otherMemberProfile.getGender().toString());
            Assertions.assertThat(basicMemberInfo.height()).isEqualTo(otherMemberProfile.getHeight());
            Assertions.assertThat(basicMemberInfo.job()).isEqualTo(jobName.toString());
            Assertions.assertThat(basicMemberInfo.hobbies().size()).isEqualTo(2);
            Assertions.assertThat(basicMemberInfo.mbti()).isEqualTo(otherMemberProfile.getMbti().toString());
            Assertions.assertThat(basicMemberInfo.city())
                .isEqualTo(otherMemberProfile.getRegion().getCity().toString());
            Assertions.assertThat(basicMemberInfo.smokingStatus())
                .isEqualTo(otherMemberProfile.getSmokingStatus().toString());
            Assertions.assertThat(basicMemberInfo.drinkingStatus())
                .isEqualTo(otherMemberProfile.getDrinkingStatus().toString());
            Assertions.assertThat(basicMemberInfo.highestEducation())
                .isEqualTo(otherMemberProfile.getHighestEducation().toString());
            Assertions.assertThat(basicMemberInfo.religion()).isEqualTo(otherMemberProfile.getReligion().toString());
            Assertions.assertThat(basicMemberInfo.like()).isEqualTo(likeLevel.toString());
        }

        private void assertionsMatchInfo(MatchInfo matchInfo, Match match) {
            Assertions.assertThat(matchInfo.matchId()).isEqualTo(match.getId());
            Assertions.assertThat(matchInfo.responderId()).isEqualTo(otherMember.getId());
            Assertions.assertThat(matchInfo.requestMessage()).isEqualTo(match.getRequestMessage().getValue());
            Assertions.assertThat(matchInfo.responseMessage())
                .isEqualTo(match.getResponseMessage() == null ? null : match.getResponseMessage().getValue());
            Assertions.assertThat(matchInfo.matchStatus()).isEqualTo(match.getStatus().toString());
            Assertions.assertThat(matchInfo.contactType()).isEqualTo(otherMember.getPrimaryContactType().toString());


            if (!match.getStatus().equals(MatchStatus.MATCHED)) {
                Assertions.assertThat(matchInfo.contact()).isNull(); // 매치가 성사되지 않았으므로.
            } else if (otherMember.getPrimaryContactType().equals(PrimaryContactType.PHONE_NUMBER)) {
                Assertions.assertThat(matchInfo.contact()).isEqualTo(otherMember.getPhoneNumber());
            } else if (otherMember.getPrimaryContactType().equals(PrimaryContactType.KAKAO)) {
                Assertions.assertThat(matchInfo.contact()).isEqualTo(otherMember.getKakaoId());
            }
        }
    }

    @Nested
    @DisplayName("유저의 인터뷰 질문/답변 조회")
    class Interviews {

        static MockedStatic<Events> mockedEvents;
        Long memberId = 10L;
        Long otherMemberId = 5L;
        List<InterviewQuestion> questions;
        List<InterviewAnswer> answers;

        @BeforeEach
        void setUp() {
            mockedEvents = Mockito.mockStatic(Events.class);
            mockedEvents.when(() -> Events.raise(Mockito.any()))
                .thenAnswer(invocation -> null);

            InterviewQuestion interviewQuestion1 = InterviewQuestion.of("인터뷰 질문 내용1", InterviewCategory.PERSONAL, true);
            InterviewQuestion interviewQuestion2 = InterviewQuestion.of("인터뷰 질문 내용2", InterviewCategory.ROMANTIC, true);
            InterviewQuestion interviewQuestion3 = InterviewQuestion.of("인터뷰 질문 내용3", InterviewCategory.PERSONAL, true);
            InterviewQuestion interviewQuestion4 = InterviewQuestion.of("인터뷰 질문 내용4", InterviewCategory.PERSONAL,
                false);

            entityManager.persist(interviewQuestion1);
            entityManager.persist(interviewQuestion2);
            entityManager.persist(interviewQuestion3);
            entityManager.persist(interviewQuestion4);
            entityManager.flush();

            InterviewAnswer interviewAnswer1 = InterviewAnswer.of(interviewQuestion1.getId(), memberId, "인터뷰 질문 답변1");
            InterviewAnswer interviewAnswer2 = InterviewAnswer.of(interviewQuestion2.getId(), memberId, "인터뷰 질문 답변2");
            InterviewAnswer interviewAnswer4 = InterviewAnswer.of(interviewQuestion2.getId(), memberId, "인터뷰 질문 답변4");

            InterviewAnswer otherMemberInterviewAnswer = InterviewAnswer.of(interviewQuestion1.getId(), otherMemberId,
                "인터뷰 질문 답변1");

            entityManager.persist(interviewAnswer1);
            entityManager.persist(interviewAnswer2);
            entityManager.persist(interviewAnswer4);
            entityManager.persist(otherMemberInterviewAnswer);
            entityManager.flush();

            questions = List.of(interviewQuestion1, interviewQuestion2, interviewQuestion3, interviewQuestion4);
            answers = List.of(interviewAnswer1, interviewAnswer2, interviewAnswer4);
        }

        @AfterEach
        void tearDown() {
            mockedEvents.close();
        }

        @Test
        @DisplayName("해당 아이디를 가진 멤버의 인터뷰가 존재하지 않는 경우, 빈 값을 얻는다.")
        void getEmptyListWhenMemberHasNoInterviews() {
            // Given
            Long notExistsMemberId = 100L;

            // When
            List<InterviewResultView> interviewResultViewList = memberQueryRepository.findInterviewsByMemberId(
                notExistsMemberId);

            // Then
            Assertions.assertThat(interviewResultViewList.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("인터뷰에 답변한 경우, 해당 멤버의 공개 질문과 답변을 모두 조회한다.")
        void getInterviewResultViewsWhenAnswersExist() {
            // Given
            List<InterviewQuestion> publicQuestions = questions.stream().filter(q -> q.isPublic()).toList();


            // When
            List<InterviewResultView> interviewResultViewList = memberQueryRepository.findInterviewsByMemberId(
                memberId);
            InterviewResultView interviewResultView1 = interviewResultViewList.get(0);
            InterviewResultView interviewResultView2 = interviewResultViewList.get(1);

            // Then
            Assertions.assertThat(interviewResultViewList.size()).isEqualTo(publicQuestions.size());

            Assertions.assertThat(interviewResultView1).isNotNull();
            Assertions.assertThat(interviewResultView2).isNotNull();

            Assertions.assertThat(interviewResultView1.content()).isEqualTo(publicQuestions.get(0).getContent());
            Assertions.assertThat(interviewResultView2.content()).isEqualTo(publicQuestions.get(1).getContent());

            Assertions.assertThat(interviewResultView1.category()).isEqualTo(questions.get(0).getCategory().toString());
            Assertions.assertThat(interviewResultView2.category()).isEqualTo(questions.get(1).getCategory().toString());

            Assertions.assertThat(interviewResultView1.answer()).isEqualTo(answers.get(0).getContent());
            Assertions.assertThat(interviewResultView2.answer()).isEqualTo(answers.get(1).getContent());
        }
    }

    @Nested
    @DisplayName("멤버 하트 잔액 조회")
    class findHeartBalanceByMemberId {
        @Test
        @DisplayName("존재하지 않는 멤버의 경우, null을 반환한다.")
        void returnNullWhenMemberIsNotExists() {
            // given
            Long notExistsMemberId = 100L;

            // when
            HeartBalanceView heartBalanceView = memberQueryRepository.findHeartBalanceByMemberId(notExistsMemberId);

            // then
            Assertions.assertThat(heartBalanceView).isNull();
        }

        @Test
        @DisplayName("존재하는 멤버의 경우, 하트 잔액 view를 반환한다.")
        void returnHeartBalanceViewWhenMemberIsExists() {
            // given
            Member member = Member.fromPhoneNumber("01012345678");
            HeartAmount purchaseHeartAmount = HeartAmount.from(10L);
            member.gainPurchaseHeart(purchaseHeartAmount);
            HeartAmount missionHeartAmount = HeartAmount.from(5L);
            member.gainMissionHeart(missionHeartAmount);
            entityManager.persist(member);
            entityManager.flush();

            // when
            HeartBalanceView heartBalanceView = memberQueryRepository.findHeartBalanceByMemberId(member.getId());

            // then
            Assertions.assertThat(heartBalanceView.purchaseHeartBalance()).isEqualTo(purchaseHeartAmount.getAmount());
            Assertions.assertThat(heartBalanceView.missionHeartBalance()).isEqualTo(missionHeartAmount.getAmount());
            Assertions.assertThat(heartBalanceView.totalHeartBalance())
                .isEqualTo(purchaseHeartAmount.getAmount() + missionHeartAmount.getAmount());
        }
    }
}
