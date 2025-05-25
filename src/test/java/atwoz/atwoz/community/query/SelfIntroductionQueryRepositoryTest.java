package atwoz.atwoz.community.query;

import atwoz.atwoz.common.config.QueryDslConfig;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.community.command.domain.selfintroduction.SelfIntroduction;
import atwoz.atwoz.community.presentation.selfintroduction.dto.AdminSelfIntroductionSearchCondition;
import atwoz.atwoz.community.presentation.selfintroduction.dto.SelfIntroductionSearchCondition;
import atwoz.atwoz.community.query.selfintroduction.SelfIntroductionQueryRepository;
import atwoz.atwoz.community.query.selfintroduction.view.AdminSelfIntroductionView;
import atwoz.atwoz.community.query.selfintroduction.view.SelfIntroductionSummaryView;
import atwoz.atwoz.community.query.selfintroduction.view.SelfIntroductionView;
import atwoz.atwoz.like.command.domain.Like;
import atwoz.atwoz.like.command.domain.LikeLevel;
import atwoz.atwoz.member.command.domain.member.*;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
import atwoz.atwoz.member.command.domain.member.vo.Region;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import atwoz.atwoz.member.query.member.AgeConverter;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDslConfig.class, SelfIntroductionQueryRepository.class})
public class SelfIntroductionQueryRepositoryTest {

    @Autowired
    private SelfIntroductionQueryRepository selfIntroductionQueryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("셀프 소개 페이지네이션 테스트")
    class selfIntroductionPaginationTest {

        static MockedStatic<Events> mockedEvents;

        Member maleMember;
        String maleMemberProfileImageUrl = "imageUrl1";

        Member femaleMember;
        String femaleMemberProfileImageUrl = "imageUrl2";

        List<SelfIntroduction> selfIntroductions = new ArrayList<>();

        @BeforeEach
        void setUp() {
            mockedEvents = Mockito.mockStatic(Events.class);
            mockedEvents.when(() -> Events.raise(Mockito.any()))
                .thenAnswer(invocation -> null);

            maleMember = Member.fromPhoneNumber("01012345678");
            femaleMember = Member.fromPhoneNumber("01012345679");

            MemberProfile maleMemberProfile = MemberProfile.builder()
                .gender(Gender.MALE)
                .region(Region.of(District.GANGBUK_GU))
                .yearOfBirth(AgeConverter.toYearOfBirth(25))
                .nickname(Nickname.from("male"))
                .build();

            MemberProfile femaleMemberProfile = MemberProfile.builder()
                .gender(Gender.FEMALE)
                .region(Region.of(District.DONG_GU_DAEJEON))
                .yearOfBirth(AgeConverter.toYearOfBirth(35))
                .nickname(Nickname.from("female"))
                .build();

            maleMember.updateProfile(maleMemberProfile);
            femaleMember.updateProfile(femaleMemberProfile);

            entityManager.persist(maleMember);
            entityManager.persist(femaleMember);
            entityManager.flush();

            // 프로필 이미지 설정.
            ProfileImage maleMemberProfileImage = ProfileImage.builder()
                .isPrimary(true)
                .order(1)
                .imageUrl(ImageUrl.from(maleMemberProfileImageUrl))
                .memberId(maleMember.getId())
                .build();

            ProfileImage femaleMemberProfileImage = ProfileImage.builder()
                .isPrimary(true)
                .imageUrl(ImageUrl.from(femaleMemberProfileImageUrl))
                .memberId(femaleMember.getId())
                .build();

            entityManager.persist(maleMemberProfileImage);
            entityManager.persist(femaleMemberProfileImage);
            entityManager.flush();

            // 셀프 소개 글 작성.
            // 남자 6개, 여자 6개
            for (int i = 0; i < 6; i++) {
                SelfIntroduction maleSelfIntroduction = SelfIntroduction.write(maleMember.getId(), "제목" + i,
                    "내용은 30자 이상 이어야 합니다. 30자 채우기용 30자 채우기용 " + i);
                SelfIntroduction femaleSelfIntroduction = SelfIntroduction.write(femaleMember.getId(), "제목" + i,
                    "내용은 30자 이상 이어야 합니다. 30자 채우기용 30자 채우기용 " + i);

                selfIntroductions.add(maleSelfIntroduction);
                selfIntroductions.add(femaleSelfIntroduction);

                entityManager.persist(maleSelfIntroduction);
                entityManager.persist(femaleSelfIntroduction);
            }
            entityManager.flush();

            selfIntroductions.sort((s1, s2) -> (int) (s2.getId() - s1.getId()));
        }

        @AfterEach
        void tearDown() {
            mockedEvents.close();
        }

        @Test
        @DisplayName("검색 조건이 없는 경우, 모든 글을 불러온다.")
        void getAllSelfIntroductions() {
            // Given
            SelfIntroductionSearchCondition searchCondition = new SelfIntroductionSearchCondition(
                null, null, null, null
            );

            // When
            List<SelfIntroductionSummaryView> selfIntroductionPage1 = selfIntroductionQueryRepository
                .findSelfIntroductions(searchCondition, null);
            List<SelfIntroductionSummaryView> selfIntroductionPage2 = selfIntroductionQueryRepository
                .findSelfIntroductions(searchCondition, selfIntroductionPage1.getLast().id());

            // Then
            assertThat(selfIntroductionPage1).isNotNull();
            assertThat(selfIntroductionPage2).isNotNull();
            assertThat(selfIntroductionPage1.size() + selfIntroductionPage2.size())
                .isEqualTo(selfIntroductions.size());
        }

        @Test
        @DisplayName("검색 조건 중 성별에 남성을 명시한 경우, 남성의 글을 불러온다.")
        void getSelfIntroductionsByGender() {
            // Given
            SelfIntroductionSearchCondition searchCondition = new SelfIntroductionSearchCondition(
                null, null, null, Gender.MALE
            );
            List<SelfIntroduction> maleSelfIntroduction = selfIntroductions.stream()
                .filter(s -> s.getMemberId() == maleMember.getId())
                .toList();

            // When
            List<SelfIntroductionSummaryView> selfIntroductionPage = selfIntroductionQueryRepository
                .findSelfIntroductions(searchCondition, null);

            // Then
            assertThat(selfIntroductionPage).isNotNull().hasSameSizeAs(maleSelfIntroduction);

            for (int i = 0; i < selfIntroductionPage.size(); i++) {
                SelfIntroductionSummaryView view = selfIntroductionPage.get(i);
                SelfIntroduction selfIntroduction = maleSelfIntroduction.get(i);

                assertThat(view.id()).isEqualTo(selfIntroduction.getId());
                assertThat(view.title()).isEqualTo(selfIntroduction.getTitle());
                assertThat(view.nickname()).isEqualTo(maleMember.getProfile().getNickname().getValue());
                assertThat(view.profileUrl()).isEqualTo(maleMemberProfileImageUrl);
                assertThat(view.yearOfBirth()).isEqualTo(maleMember.getProfile().getYearOfBirth().getValue());
            }
        }

        @Test
        @DisplayName("검색 조건 중 최대 연도를 명시한 경우, 해당 연도 이하의 멤버가 작성한 글을 불러온다.")
        void getSelfIntroductionsByStartAgeCondition() {
            // Given
            int toYearOfBirth = maleMember.getProfile().getYearOfBirth().getValue() + 1;
            SelfIntroductionSearchCondition searchCondition = new SelfIntroductionSearchCondition(
                null, null, toYearOfBirth, null
            );

            // When
            List<SelfIntroductionSummaryView> selfIntroductionPage = selfIntroductionQueryRepository
                .findSelfIntroductions(searchCondition, null);

            // Then
            assertThat(selfIntroductionPage).isNotNull();
            for (SelfIntroductionSummaryView view : selfIntroductionPage) {
                assertThat(view.yearOfBirth()).isLessThanOrEqualTo(toYearOfBirth);
            }
        }

        @Test
        @DisplayName("검색 조건 중 최소 연도를 명시한 경우, 해당 연도 이상의 멤버가 작성한 글을 불러온다.")
        void getSelfIntroductionsByEndAgeCondition() {
            // Given
            int fromYearOfBirth = femaleMember.getProfile().getYearOfBirth().getValue() - 1;
            SelfIntroductionSearchCondition searchCondition = new SelfIntroductionSearchCondition(
                null, fromYearOfBirth, null, null
            );


            // When
            List<SelfIntroductionSummaryView> selfIntroductionPage = selfIntroductionQueryRepository
                .findSelfIntroductions(searchCondition, null);

            // Then
            assertThat(selfIntroductionPage).isNotNull();
            for (SelfIntroductionSummaryView view : selfIntroductionPage) {
                assertThat(view.yearOfBirth()).isGreaterThanOrEqualTo(fromYearOfBirth);
            }
        }

        @Test
        @DisplayName("검색 조건 중 최소/최대 연도를 명시한 경우, 해당 연도 사이의 멤버가 작성한 글을 불러온다.")
        void getSelfIntroductionsByYearRangeCondition() {
            // Given
            int toYearOfBirth = maleMember.getProfile().getYearOfBirth().getValue();
            int fromYearOfBirth = femaleMember.getProfile().getYearOfBirth().getValue();

            SelfIntroductionSearchCondition searchCondition = new SelfIntroductionSearchCondition(
                null, fromYearOfBirth, toYearOfBirth, null
            );

            // When
            List<SelfIntroductionSummaryView> selfIntroductionPage = selfIntroductionQueryRepository
                .findSelfIntroductions(searchCondition, null);

            // Then
            assertThat(selfIntroductionPage).isNotNull();
            for (SelfIntroductionSummaryView view : selfIntroductionPage) {
                assertThat(view.yearOfBirth()).isBetween(fromYearOfBirth, toYearOfBirth);
            }
        }

        @Test
        @DisplayName("검색 조건 중 지역을 명시한 경우, 해당 지역 출신의 멤벅 작성한 글을 불러온다.")
        void getSelfIntroductionByRegionCondition() {
            // Given
            City city = maleMember.getProfile().getRegion().getCity();
            SelfIntroductionSearchCondition searchCondition = new SelfIntroductionSearchCondition(
                List.of(city), null, null, null
            );

            List<SelfIntroduction> maleSelfIntroduction = selfIntroductions.stream()
                .filter(s -> s.getMemberId() == maleMember.getId())
                .toList();

            // When
            List<SelfIntroductionSummaryView> selfIntroductionPage = selfIntroductionQueryRepository
                .findSelfIntroductions(searchCondition, null);

            // Then
            assertThat(selfIntroductionPage).isNotNull();
            for (int i = 0; i < selfIntroductionPage.size(); i++) {
                SelfIntroductionSummaryView view = selfIntroductionPage.get(i);
                SelfIntroduction selfIntroduction = maleSelfIntroduction.get(i);

                assertThat(view.id()).isEqualTo(selfIntroduction.getId());
            }
        }

        @Test
        @DisplayName("모든 조건을 설정해 글을 조회합니다.")
        void findSelfIntroductions() {
            // Given
            SelfIntroductionSearchCondition searchCondition = new SelfIntroductionSearchCondition(
                List.of(City.DAEJEON, City.SEOUL), femaleMember.getProfile().getYearOfBirth().getValue(),
                maleMember.getProfile().getYearOfBirth().getValue(), maleMember.getProfile().getGender()
            );
            List<SelfIntroduction> maleSelfIntroduction = selfIntroductions.stream()
                .filter(s -> s.getMemberId() == maleMember.getId())
                .toList();

            // When
            List<SelfIntroductionSummaryView> selfIntroductionPage = selfIntroductionQueryRepository
                .findSelfIntroductions(searchCondition, null);

            // Then
            assertThat(selfIntroductionPage).isNotNull();
            for (int i = 0; i < selfIntroductionPage.size(); i++) {
                SelfIntroductionSummaryView view = selfIntroductionPage.get(i);
                SelfIntroduction selfIntroduction = maleSelfIntroduction.get(i);

                assertThat(view.id()).isEqualTo(selfIntroduction.getId());
            }
        }
    }

    @Nested
    @DisplayName("셀프 소개 상세 조회 테스트")
    class selfIntroductionFindTest {

        static MockedStatic<Events> mockedEvents;

        Member member;
        Member targetMember;
        ProfileImage profileImage;
        Like like;
        Set<Hobby> hobbies;
        SelfIntroduction selfIntroduction;


        @BeforeEach
        void setUp() {
            mockedEvents = Mockito.mockStatic(Events.class);
            mockedEvents.when(() -> Events.raise(Mockito.any()))
                .thenAnswer(invocation -> null);

            // 취미 데이터 생성.
            Hobby hobby = Hobby.ANIMATION;
            Hobby hobby2 = Hobby.BOARD_GAMES;
            hobbies = Set.of(hobby, hobby2);

            // 멤버 데이터 생성.
            member = Member.fromPhoneNumber("01012345678");
            targetMember = Member.fromPhoneNumber("01056781234");

            MemberProfile memberProfile = MemberProfile.builder()
                .mbti(Mbti.ENFJ)
                .region(Region.of(District.GANGBUK_GU))
                .nickname(Nickname.from("닉네임"))
                .yearOfBirth(AgeConverter.toYearOfBirth(25))
                .hobbies(Set.of(hobby, hobby2))
                .build();

            targetMember.updateProfile(memberProfile);

            entityManager.persist(member);
            entityManager.persist(targetMember);
            entityManager.flush();

            // 프로필 이미지 설정.
            profileImage = ProfileImage.builder()
                .imageUrl(ImageUrl.from("imageUrl1"))
                .memberId(targetMember.getId())
                .isPrimary(true)
                .order(1)
                .build();

            ProfileImage subImage = ProfileImage.builder()
                .imageUrl(ImageUrl.from("imageUrl2"))
                .memberId(targetMember.getId())
                .isPrimary(false)
                .order(2)
                .build();


            entityManager.persist(profileImage);
            entityManager.persist(subImage);
            entityManager.flush();


            // 좋아요 데이터 생성
            like = Like.of(member.getId(), targetMember.getId(), LikeLevel.HIGHLY_INTERESTED);
            entityManager.persist(like);

            // 셀프 소개 데이터 생성.
            selfIntroduction = SelfIntroduction.write(
                targetMember.getId(), "제목", "내용은 50자를 넘어야합니다. 50자를 넘어야 합니다. 50자를 넘어야.."
            );
            entityManager.persist(selfIntroduction);

            entityManager.flush();
        }

        @AfterEach
        void tearDown() {
            mockedEvents.close();
        }

        @Test
        @DisplayName("셀프 소개를 상세 조회합니다.")
        void findSelfIntroduction() {
            // Given
            Long memberId = member.getId();
            Long selfIntroductionId = selfIntroduction.getId();

            System.out.println(memberId);
            System.out.println(selfIntroductionId);

            // When
            SelfIntroductionView view = selfIntroductionQueryRepository.findSelfIntroductionByIdWithMemberId(
                selfIntroductionId, memberId).orElse(null);

            // Then
            assertThat(view.title()).isEqualTo(selfIntroduction.getTitle());
            assertThat(view.content()).isEqualTo(selfIntroduction.getContent());
            assertThat(view.like()).isEqualTo(like.getLevel().toString());
            assertThat(view.memberBasicInfo().memberId()).isEqualTo(targetMember.getId());
            assertThat(view.memberBasicInfo().age())
                .isEqualTo(AgeConverter.toAge(targetMember.getProfile().getYearOfBirth().getValue()));
            assertThat(view.memberBasicInfo().mbti())
                .isEqualTo(targetMember.getProfile().getMbti().toString());
            assertThat(view.memberBasicInfo().nickname())
                .isEqualTo(targetMember.getProfile().getNickname().getValue());
            assertThat(view.memberBasicInfo().city())
                .isEqualTo(targetMember.getProfile().getRegion().getCity().toString());
            assertThat(view.memberBasicInfo().district())
                .isEqualTo(targetMember.getProfile().getRegion().getDistrict().toString());
            assertThat(view.memberBasicInfo().hobbies())
                .hasSameSizeAs(targetMember.getProfile().getHobbies());
            assertThat(view.memberBasicInfo().profileImageUrl()).isEqualTo(profileImage.getUrl());
        }

        @Test
        @DisplayName("존재하지 않는 셀프 소개의 경우, 빈 값을 반환합니다.")
        void getEmptyValueWhenSelfIntroductionIsNotExists() {
            // Given
            Long notExistsSelfIntroductionId = 100L;
            Long memberId = member.getId();

            // When & Then
            assertThat(
                selfIntroductionQueryRepository.findSelfIntroductionByIdWithMemberId(notExistsSelfIntroductionId,
                    memberId)).isEmpty();
        }
    }

    @Nested
    @DisplayName("관리자용 셀프 소개 페이지네이션")
    class selfIntroductionAdminPaginationTest {
        private static final int PAGE_SIZE = 5;
        Member manMember;
        Member womanMember;
        List<SelfIntroduction> selfIntroductionsByMan;
        List<SelfIntroduction> selfIntroductionsByWoman;

        @BeforeEach
        void setUp() {
            manMember = Member.fromPhoneNumber("01012345678");
            womanMember = Member.fromPhoneNumber("01098765432");

            MemberProfile manProfile = MemberProfile
                .builder()
                .nickname(Nickname.from("남자닉네임"))
                .gender(Gender.MALE)
                .build();

            MemberProfile womanProfile = MemberProfile
                .builder()
                .nickname(Nickname.from("여자닉네임"))
                .gender(Gender.FEMALE)
                .build();

            manMember.updateProfile(manProfile);
            womanMember.updateProfile(womanProfile);

            entityManager.persist(manMember);
            entityManager.persist(womanMember);
            entityManager.flush();

            selfIntroductionsByMan = new ArrayList<>();
            selfIntroductionsByWoman = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                SelfIntroduction selfIntroductionByMan = SelfIntroduction.write(manMember.getId(), "제목" + i,
                    "내용은 30자 이상이어야합니다... 30자를 채우자!! " + i);
                SelfIntroduction selfIntroductionByWoman = SelfIntroduction.write(womanMember.getId(), "제목입니다." + i,
                    "내용입니다. 내용은 30자 이상이어야합니다... 30자를 채우자!! " + i);

                if (i % 2 == 0) {
                    selfIntroductionByMan.close();
                    selfIntroductionByWoman.close();
                }

                entityManager.persist(selfIntroductionByMan);
                entityManager.persist(selfIntroductionByWoman);

                selfIntroductionsByMan.add(selfIntroductionByMan);
                selfIntroductionsByWoman.add(selfIntroductionByWoman);
            }

            entityManager.flush();
            entityManager.clear();
        }

        @DisplayName("모든 셀프 소개를 조회합니다.")
        void findAllSelfIntroductions() {
            // Given
            AdminSelfIntroductionSearchCondition condition = new AdminSelfIntroductionSearchCondition(
                null,
                null,
                null,
                null,
                null
            );
            PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);

            // When
            Page<AdminSelfIntroductionView> selfIntroductionViews = selfIntroductionQueryRepository.findSelfIntroductions(
                condition, pageRequest);

            // Then
            AssertionsForClassTypes.assertThat(selfIntroductionViews.getTotalElements()).isEqualTo(
                selfIntroductionsByWoman.size() + selfIntroductionsByMan.size());
        }

        @Test
        @DisplayName("공개 여부로 셀프 소개를 조회합니다.")
        void findSelfIntroductionsByOpenedFlag() {
            // Given
            AdminSelfIntroductionSearchCondition condition = new AdminSelfIntroductionSearchCondition(
                true,
                null,
                null,
                null,
                null
            );
            PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);

            // When
            Page<AdminSelfIntroductionView> selfIntroductionViews = selfIntroductionQueryRepository.findSelfIntroductions(
                condition, pageRequest);

            // Then
            for (AdminSelfIntroductionView view : selfIntroductionViews.getContent()) {
                AssertionsForClassTypes.assertThat(view.isOpened()).isTrue();
            }
        }

        @Test
        @DisplayName("닉네임으로 셀프 소개를 조회합니다.")
        void findSelfIntroductionsByNickname() {
            // Given
            AdminSelfIntroductionSearchCondition condition = new AdminSelfIntroductionSearchCondition(
                null,
                manMember.getProfile().getNickname().getValue(),
                null,
                null,
                null
            );
            PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);

            // When
            Page<AdminSelfIntroductionView> selfIntroductionViews = selfIntroductionQueryRepository.findSelfIntroductions(
                condition, pageRequest);

            // Then
            AssertionsForClassTypes.assertThat(selfIntroductionViews.getTotalElements())
                .isEqualTo(selfIntroductionsByMan.size());
            for (AdminSelfIntroductionView view : selfIntroductionViews.getContent()) {
                AssertionsForClassTypes.assertThat(view.nickname())
                    .isEqualTo(manMember.getProfile().getNickname().getValue());
            }
        }

        @Test
        @DisplayName("전화번호로 셀프 소개를 조회합니다.")
        void findSelfIntroductionsByPhoneNumber() {
            // Given
            AdminSelfIntroductionSearchCondition condition = new AdminSelfIntroductionSearchCondition(
                null,
                null,
                womanMember.getPhoneNumber(),
                null,
                null
            );
            PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);

            // When
            Page<AdminSelfIntroductionView> selfIntroductionViews = selfIntroductionQueryRepository.findSelfIntroductions(
                condition, pageRequest);

            // Then
            AssertionsForClassTypes.assertThat(selfIntroductionViews.getTotalElements()).isEqualTo(PAGE_SIZE);

            for (int i = 0; i < PAGE_SIZE; i++) {
                SelfIntroduction selfIntroductionByWoman = selfIntroductionsByWoman.get(i);
                AdminSelfIntroductionView view = selfIntroductionViews.getContent().get(i);
                AssertionsForClassTypes.assertThat(view.nickname())
                    .isEqualTo(womanMember.getProfile().getNickname().getValue());
                AssertionsForClassTypes.assertThat(view.selfIntroductionId())
                    .isEqualTo(selfIntroductionByWoman.getId());
                AssertionsForClassTypes.assertThat(view.gender())
                    .isEqualTo(womanMember.getProfile().getGender().name());
                AssertionsForClassTypes.assertThat(view.content()).isEqualTo(selfIntroductionByWoman.getContent());
                AssertionsForClassTypes.assertThat(view.createdDate())
                    .isEqualTo(toDate(selfIntroductionByWoman.getCreatedAt()));
                AssertionsForClassTypes.assertThat(view.updatedDate())
                    .isEqualTo(toDate(selfIntroductionByWoman.getUpdatedAt()));
                AssertionsForClassTypes.assertThat(view.deletedDate())
                    .isEqualTo(toDate(selfIntroductionByWoman.getDeletedAt()));
            }
        }

        @Test
        @DisplayName("시작일과 종료일을 설정해 셀프 소개를 조회합니다.")
        void findSelfIntroductionsByStartDateAndEndDate() {
            // Given
            AdminSelfIntroductionSearchCondition condition = new AdminSelfIntroductionSearchCondition(
                null,
                null,
                womanMember.getPhoneNumber(),
                LocalDate.EPOCH,
                LocalDate.now().plusDays(1)
            );
            PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);

            // When
            Page<AdminSelfIntroductionView> selfIntroductionViews = selfIntroductionQueryRepository.findSelfIntroductions(
                condition, pageRequest);

            // Then
            AssertionsForClassTypes.assertThat(selfIntroductionViews.getTotalElements()).isEqualTo(PAGE_SIZE);
        }

        private String toDate(LocalDateTime localDateTime) {
            return localDateTime != null ? DateTimeFormatter.ofPattern("yyyy/MM/dd").format(localDateTime) : null;
        }
    }
}
