package atwoz.atwoz.member.query.member.infra;

import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.admin.command.domain.warning.Warning;
import atwoz.atwoz.admin.command.domain.warning.WarningReasonType;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartAmount;
import atwoz.atwoz.interview.command.domain.answer.InterviewAnswer;
import atwoz.atwoz.interview.command.domain.question.InterviewCategory;
import atwoz.atwoz.interview.command.domain.question.InterviewQuestion;
import atwoz.atwoz.member.command.domain.member.*;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
import atwoz.atwoz.member.command.domain.member.vo.Region;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import atwoz.atwoz.member.query.member.condition.AdminMemberSearchCondition;
import atwoz.atwoz.member.query.member.view.*;
import atwoz.atwoz.notification.command.domain.NotificationPreference;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DataJpaTest
@Import({QuerydslConfig.class, AdminMemberQueryRepository.class})
class AdminMemberQueryRepositoryTest {

    private static MockedStatic<Events> mockedEvents;

    @Autowired
    private AdminMemberQueryRepository adminMemberQueryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        mockedEvents = Mockito.mockStatic(Events.class);
        mockedEvents.when(() -> Events.raise(Mockito.any()))
            .thenAnswer(invocation -> null);
    }

    @AfterEach
    void tearDown() {
        mockedEvents.close();
    }

    private Member createMember(String phoneNumber, String nickname, ActivityStatus activityStatus, Gender gender,
        Grade grade) {
        Member member = Member.fromPhoneNumber(phoneNumber);
        if (activityStatus.equals(ActivityStatus.DORMANT)) {
            member.changeToDormant();
        }
        member.gainPurchaseHeart(HeartAmount.from(100L));
        member.gainMissionHeart(HeartAmount.from(50L));
        member.updateGrade(grade);
        MemberProfile memberProfile = MemberProfile.builder()
            .nickname(Nickname.from(nickname))
            .gender(gender)
            .job(Job.OTHERS)
            .drinkingStatus(DrinkingStatus.NONE)
            .region(Region.of(District.ANDONG_SI))
            .height(175)
            .mbti(Mbti.ENFJ)
            .highestEducation(HighestEducation.DOCTORATE)
            .hobbies(Set.of(Hobby.ANIMATION, Hobby.BOARD_GAMES))
            .religion(Religion.BUDDHIST)
            .smokingStatus(SmokingStatus.NONE)
            .yearOfBirth(1997)
            .build();
        member.updateProfile(memberProfile);
        entityManager.persist(member);
        return member;
    }

    private Warning createWarning(long adminId, long memberId) {
        var warning = Warning.issue(adminId, memberId, 0, List.of(WarningReasonType.INAPPROPRIATE_CONTENT));
        entityManager.persist(warning);
        return warning;
    }

    @Nested
    @DisplayName("멤버 페이지네이션 조회 테스트")
    class AdminMemberPageTest {

        @Test
        @DisplayName("멤버 페이지네이션 조회 성공")
        void findMembers() {
            // given
            Member member1 = createMember("01011111111", "테스트유저1", ActivityStatus.ACTIVE, Gender.MALE, Grade.SILVER);
            Member member2 = createMember("01022222222", "테스트유저2", ActivityStatus.DORMANT, Gender.FEMALE,
                Grade.DIAMOND);
            final long adminId = 500L;
            createWarning(adminId, member1.getId());
            createWarning(adminId, member1.getId());
            entityManager.flush();

            AdminMemberSearchCondition condition = new AdminMemberSearchCondition(null, null, null, null, null, null,
                null);
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<AdminMemberView> result = adminMemberQueryRepository.findMembers(condition, pageable);

            // then
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent()).hasSize(2);
            AdminMemberView memberView1 = result.getContent().get(0);
            AdminMemberView memberView2 = result.getContent().get(1);
            if (memberView1.memberId() == member1.getId()) {
                assertMemberView(memberView1, member1, 2);
                assertMemberView(memberView2, member2, 0);
            } else {
                assertMemberView(memberView2, member1, 2);
                assertMemberView(memberView1, member2, 0);
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {"phoneNumber", "nickname", "activityStatus", "gender", "grade"})
        @DisplayName("멤버 페이지네이션 조회 - 조건에 따른 필터링")
        void findMembersWithConditions(String conditionType) {
            // given
            Member findedMember = createMember("01011111111", "테스트유저1", ActivityStatus.ACTIVE, Gender.MALE,
                Grade.SILVER);
            createMember("01022222222", "테스트유저2", ActivityStatus.DORMANT, Gender.FEMALE,
                Grade.DIAMOND);
            entityManager.flush();

            AdminMemberSearchCondition condition = new AdminMemberSearchCondition(
                conditionType.equals("activityStatus") ? findedMember.getActivityStatus().name() : null,
                conditionType.equals("gender") ? findedMember.getGender().name() : null,
                conditionType.equals("grade") ? findedMember.getGrade().name() : null,
                conditionType.equals("nickname") ? findedMember.getProfile().getNickname().getValue() : null,
                conditionType.equals("phoneNumber") ? findedMember.getPhoneNumber() : null,
                null, null
            );
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<AdminMemberView> result = adminMemberQueryRepository.findMembers(condition, pageable);

            // then
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);
            AdminMemberView memberView = result.getContent().get(0);
            assertMemberView(memberView, findedMember, 0);
        }

        @Test
        @DisplayName("멤버 페이지네이션 조회 - 날짜 조건 필터링")
        void findMembersWithDateConditions() {
            // given
            Member member = createMember("01011111111", "테스트유저1", ActivityStatus.ACTIVE, Gender.MALE, Grade.SILVER);
            entityManager.flush();

            AdminMemberSearchCondition createdAtAfterStartDateCondition = new AdminMemberSearchCondition(null, null,
                null, null, null, member.getCreatedAt().toLocalDate().plusDays(1), null);
            AdminMemberSearchCondition createdAtEqStartDateCondition = new AdminMemberSearchCondition(null, null,
                null, null, null, member.getCreatedAt().toLocalDate(), null);
            AdminMemberSearchCondition createdAtBeforeEndDateCondition = new AdminMemberSearchCondition(null, null,
                null, null, null, null, member.getCreatedAt().toLocalDate().minusDays(1));
            AdminMemberSearchCondition createdAtEqEndDateCondition = new AdminMemberSearchCondition(null, null,
                null, null, null, null, member.getCreatedAt().toLocalDate());
            Pageable pageable = PageRequest.of(0, 10);

            // when
            Page<AdminMemberView> resultAfterStartDate = adminMemberQueryRepository.findMembers(
                createdAtAfterStartDateCondition, pageable);
            Page<AdminMemberView> resultEqStartDate = adminMemberQueryRepository.findMembers(
                createdAtEqStartDateCondition, pageable);
            Page<AdminMemberView> resultBeforeEndDate = adminMemberQueryRepository.findMembers(
                createdAtBeforeEndDateCondition, pageable);
            Page<AdminMemberView> resultEqEndDate = adminMemberQueryRepository.findMembers(
                createdAtEqEndDateCondition, pageable);

            // then
            assertThat(resultAfterStartDate.getTotalElements()).isZero();
            assertThat(resultAfterStartDate.getContent()).isEmpty();

            assertThat(resultEqStartDate.getTotalElements()).isEqualTo(1);
            assertThat(resultEqStartDate.getContent()).hasSize(1);

            assertThat(resultBeforeEndDate.getTotalElements()).isZero();
            assertThat(resultBeforeEndDate.getContent()).isEmpty();

            assertThat(resultEqEndDate.getTotalElements()).isEqualTo(1);
            assertThat(resultEqEndDate.getContent()).hasSize(1);
        }

        private void assertMemberView(AdminMemberView memberView, Member member, int warningCount) {
            assertThat(memberView.memberId()).isEqualTo(member.getId());
            assertThat(memberView.nickname()).isEqualTo(member.getProfile().getNickname().getValue());
            assertThat(memberView.activityStatus()).isEqualTo(member.getActivityStatus().name());
            assertThat(memberView.gender()).isEqualTo(member.getGender().name());
            assertThat(memberView.joinedAt()).isCloseTo(member.getCreatedAt(), within(1, ChronoUnit.MICROS));
            assertThat(memberView.warningCount()).isEqualTo(warningCount);
        }
    }

    @Nested
    @DisplayName("멤버 단건 조회 테스트")
    class AdminMemberFindTest {

        @Test
        @DisplayName("멤버 단건 조회 성공")
        void findMemberById() {
            // given
            Member member = createMember("01011111111", "테스트유저1", ActivityStatus.ACTIVE, Gender.MALE, Grade.SILVER);
            final long adminId = 500L;
            createWarning(adminId, member.getId());
            createWarning(adminId, member.getId());

            final InterviewQuestion question1 = createInterviewQuestion("질문1");
            final InterviewAnswer answer1 = createInterviewAnswer(question1.getId(), member.getId(), "답변1");
            final InterviewQuestion question2 = createInterviewQuestion("질문2");
            final InterviewAnswer answer2 = createInterviewAnswer(question2.getId(), member.getId(), "답변2");

            final List<ProfileImage> profileImages = createProfileImages(member.getId(), "https://example.com/image");

            final NotificationPreference notificationPreference = createNotificationPreference(member.getId());

            entityManager.flush();

            // when
            AdminMemberDetailView result = adminMemberQueryRepository.findById(member.getId()).get();

            // then
            assertBasicInfo(result.basicInfo(), member);
            assertInterviewInfos(result.interviewInfos(), question1, answer1, question2, answer2);
            assertHeartBalanceView(result.heartBalanceInfo(), member);
            assertProfileImageUrls(result.profileImageUrls(), profileImages);
            assertProfileInfo(result.profileInfo(), member);
            assertAdminMemberSettingInfo(result.settingInfo(), member, notificationPreference);
            assertAdminMemberStatusInfo(result.statusInfo(), member, 2, true);
            assertDateInfo(result, member);
        }

        @Test
        @DisplayName("멤버 단건 조회 - 멤버가 없는 경우")
        void findMemberByIdWithoutMember() {
            // given
            Member member = createMember("01011111111", "테스트유저1", ActivityStatus.ACTIVE, Gender.MALE, Grade.SILVER);
            entityManager.flush();

            // when
            Optional<AdminMemberDetailView> result = adminMemberQueryRepository.findById(member.getId() + 1);

            // then
            assertThat(result).isEmpty();
        }

        private void assertBasicInfo(BasicInfo basicInfo, Member member) {
            assertThat(basicInfo.nickname()).isEqualTo(member.getProfile().getNickname().getValue());
            assertThat(basicInfo.phoneNumber()).isEqualTo(member.getPhoneNumber());
            assertThat(basicInfo.kakaoId()).isEqualTo(member.getKakaoId());
            assertThat(basicInfo.gender()).isEqualTo(member.getGender().name());
            assertThat(basicInfo.age()).isEqualTo((member.getProfile().getYearOfBirth().getAge()));
            assertThat(basicInfo.height()).isEqualTo(member.getProfile().getHeight());
        }

        private void assertInterviewInfos(List<InterviewInfoView> interviewInfos, InterviewQuestion question1,
            InterviewAnswer answer1, InterviewQuestion question2, InterviewAnswer answer2) {
            assertThat(interviewInfos).hasSize(2);
            if (interviewInfos.get(0).title().equals(question1.getContent())) {
                assertThat(interviewInfos.get(0).title()).isEqualTo(question1.getContent());
                assertThat(interviewInfos.get(0).content()).isEqualTo(answer1.getContent());
                assertThat(interviewInfos.get(1).title()).isEqualTo(question2.getContent());
                assertThat(interviewInfos.get(1).content()).isEqualTo(answer2.getContent());
            } else {
                assertThat(interviewInfos.get(0).title()).isEqualTo(question2.getContent());
                assertThat(interviewInfos.get(0).content()).isEqualTo(answer2.getContent());
                assertThat(interviewInfos.get(1).title()).isEqualTo(question1.getContent());
                assertThat(interviewInfos.get(1).content()).isEqualTo(answer1.getContent());
            }
        }

        private void assertHeartBalanceView(HeartBalanceView heartBalanceView, Member member) {
            assertThat(heartBalanceView.missionHeartBalance()).isEqualTo(
                member.getHeartBalance().getMissionHeartBalance());
            assertThat(heartBalanceView.purchaseHeartBalance()).isEqualTo(
                member.getHeartBalance().getPurchaseHeartBalance());
            assertThat(heartBalanceView.totalHeartBalance())
                .isEqualTo(member.getHeartBalance().getMissionHeartBalance() + member.getHeartBalance()
                    .getPurchaseHeartBalance());
        }

        private void assertProfileImageUrls(List<String> profileImageUrls, List<ProfileImage> profileImages) {
            assertThat(profileImageUrls).hasSize(2);
            assertThat(profileImageUrls.get(0)).isEqualTo(profileImages.get(0).getUrl());
            assertThat(profileImageUrls.get(1)).isEqualTo(profileImages.get(1).getUrl());
        }

        private void assertProfileInfo(ProfileInfo profileInfo, Member member) {
            assertThat(profileInfo.city()).isEqualTo(member.getProfile().getRegion().getCity().name());
            assertThat(profileInfo.job()).isEqualTo(member.getProfile().getJob().name());
            assertThat(profileInfo.highestEducation()).isEqualTo(member.getProfile().getHighestEducation().name());
            assertThat(profileInfo.district()).isEqualTo(member.getProfile().getRegion().getDistrict().name());
            assertThat(profileInfo.mbti()).isEqualTo(member.getProfile().getMbti().name());
            assertThat(profileInfo.religion()).isEqualTo(member.getProfile().getReligion().name());
            assertThat(profileInfo.smokingStatus()).isEqualTo(member.getProfile().getSmokingStatus().name());
            assertThat(profileInfo.drinkingStatus()).isEqualTo(member.getProfile().getDrinkingStatus().name());
            List<String> hobbies = member.getProfile().getHobbies().stream()
                .map(Hobby::name).toList();
            assertThat(profileInfo.hobbies()).containsExactlyInAnyOrderElementsOf(hobbies);
        }

        private void assertAdminMemberSettingInfo(AdminMemberSettingInfo settingInfo, Member member,
            NotificationPreference notificationPreference) {
            assertThat(settingInfo.grade()).isEqualTo(member.getGrade().name());
            assertThat(settingInfo.isProfilePublic()).isEqualTo(member.isProfilePublic());
            assertThat(settingInfo.activityStatus()).isEqualTo(member.getActivityStatus().name());
            assertThat(settingInfo.isVip()).isEqualTo(member.isVip());
            assertThat(settingInfo.isPushNotificationEnabled()).isEqualTo(notificationPreference.isEnabledGlobally());
        }

        private void assertAdminMemberStatusInfo(AdminMemberStatusInfo statusInfo, Member member, int warningCount,
            boolean hasInterviewAnswers) {
            assertThat(statusInfo.primaryContactType()).isEqualTo(member.getPrimaryContactType().name());
            assertThat(statusInfo.hasInterviewAnswer()).isEqualTo(hasInterviewAnswers);
            assertThat(statusInfo.warningCount()).isEqualTo(warningCount);
        }

        private void assertDateInfo(AdminMemberDetailView result, Member member) {
            assertThat(result.createdAt()).isCloseTo(member.getCreatedAt(), within(1, ChronoUnit.MICROS));
            assertThat(result.deletedAt()).isNull();
        }

        private InterviewQuestion createInterviewQuestion(String content) {
            InterviewQuestion question = InterviewQuestion.of(content, InterviewCategory.PERSONAL, true);
            entityManager.persist(question);
            return question;
        }

        private InterviewAnswer createInterviewAnswer(long questionId, long memberId, String content) {
            InterviewAnswer answer = InterviewAnswer.of(questionId, memberId, content);
            entityManager.persist(answer);
            return answer;
        }

        private List<ProfileImage> createProfileImages(long memberId, String imageUrl) {
            ProfileImage profileImage1 = ProfileImage.builder()
                .memberId(memberId)
                .imageUrl(ImageUrl.from(imageUrl + "1"))
                .order(1)
                .isPrimary(true)
                .build();
            entityManager.persist(profileImage1);
            ProfileImage profileImage2 = ProfileImage.builder()
                .memberId(memberId)
                .imageUrl(ImageUrl.from(imageUrl + "2"))
                .order(2)
                .isPrimary(false)
                .build();
            entityManager.persist(profileImage2);
            return List.of(profileImage1, profileImage2);
        }

        private NotificationPreference createNotificationPreference(long memberId) {
            NotificationPreference notificationPreference = NotificationPreference.of(memberId);
            entityManager.persist(notificationPreference);
            return notificationPreference;
        }
    }
}