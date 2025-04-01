package atwoz.atwoz.community.query;

import atwoz.atwoz.common.config.QueryDslConfig;
import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.community.command.domain.selfintroduction.SelfIntroduction;
import atwoz.atwoz.community.query.selfintroduction.SelfIntroductionQueryRepository;
import atwoz.atwoz.community.query.selfintroduction.SelfIntroductionSearchCondition;
import atwoz.atwoz.community.query.selfintroduction.view.SelfIntroductionSummaryView;
import atwoz.atwoz.member.command.domain.member.Gender;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.Region;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import atwoz.atwoz.member.query.member.AgeConverter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

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
                    .region(Region.SEOUL)
                    .yearOfBirth(AgeConverter.toYearOfBirth(25))
                    .build();

            MemberProfile femaleMemberProfile = MemberProfile.builder()
                    .gender(Gender.FEMALE)
                    .region(Region.DAEJEON)
                    .yearOfBirth(AgeConverter.toYearOfBirth(35))
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
                SelfIntroduction maleSelfIntroduction = SelfIntroduction.write(maleMember.getId(), "제목" + i, "내용은 30자 이상 이어야 합니다. 30자 채우기용 30자 채우기용 " + i);
                SelfIntroduction femaleSelfIntroduction = SelfIntroduction.write(femaleMember.getId(), "제목" + i, "내용은 30자 이상 이어야 합니다. 30자 채우기용 30자 채우기용 " + i);

                selfIntroductions.add(maleSelfIntroduction);
                selfIntroductions.add(femaleSelfIntroduction);

                entityManager.persist(maleSelfIntroduction);
                entityManager.persist(femaleSelfIntroduction);
            }
            entityManager.flush();

            selfIntroductions.sort((s1, s2) -> (int)(s2.getId() - s1.getId()));
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
            Page<SelfIntroductionSummaryView> selfIntroductionPage = selfIntroductionQueryRepository
                    .findSelfIntroductions(searchCondition, PageRequest.of(0, 20));

            // Then
            Assertions.assertThat(selfIntroductionPage).isNotNull();
            Assertions.assertThat(selfIntroductionPage.getTotalElements()).isEqualTo(selfIntroductions.size());
        }

        @Test
        @DisplayName("검색 조건 중 성별에 남성을 명시한 경우, 남성의 글을 불러온다.")
        void getSelfIntroductionsByGender() {
            // Given
            SelfIntroductionSearchCondition searchCondition = new SelfIntroductionSearchCondition(
                    null, null, null, Gender.MALE
            );
            List<SelfIntroduction> maleSelfIntroduction = selfIntroductions.stream().filter(s -> s.getMemberId() == maleMember.getId()).toList();


            // When
            Page<SelfIntroductionSummaryView> selfIntroductionPage = selfIntroductionQueryRepository
                    .findSelfIntroductions(searchCondition, PageRequest.of(0, 20));


            // Then
            Assertions.assertThat(selfIntroductionPage).isNotNull();
            Assertions.assertThat(selfIntroductionPage.getTotalElements()).isEqualTo(maleSelfIntroduction.size());

            for (int i = 0; i < selfIntroductionPage.getTotalElements(); i++) {
                SelfIntroductionSummaryView view = selfIntroductionPage.getContent().get(i);
                SelfIntroduction selfIntroduction = maleSelfIntroduction.get(i);

                Assertions.assertThat(view.id()).isEqualTo(selfIntroduction.getId());
                Assertions.assertThat(view.title()).isEqualTo(selfIntroduction.getTitle());
                Assertions.assertThat(view.nickname()).isEqualTo(maleMember.getProfile().getNickname());
                Assertions.assertThat(view.profileUrl()).isEqualTo(maleMemberProfileImageUrl);
                Assertions.assertThat(view.yearOfBirth()).isEqualTo(maleMember.getProfile().getYearOfBirth().getValue());
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
            Page<SelfIntroductionSummaryView> selfIntroductionPage = selfIntroductionQueryRepository
                    .findSelfIntroductions(searchCondition, PageRequest.of(0, 20));

            // Then
            Assertions.assertThat(selfIntroductionPage).isNotNull();
            for (int i = 0; i < selfIntroductionPage.getTotalElements(); i++) {
                SelfIntroductionSummaryView view = selfIntroductionPage.getContent().get(i);
                Assertions.assertThat(view.yearOfBirth()).isLessThanOrEqualTo(toYearOfBirth);
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
            Page<SelfIntroductionSummaryView> selfIntroductionPage = selfIntroductionQueryRepository
                    .findSelfIntroductions(searchCondition, PageRequest.of(0, 20));

            // Then
            Assertions.assertThat(selfIntroductionPage).isNotNull();
            for (int i = 0; i < selfIntroductionPage.getTotalElements(); i++) {
                SelfIntroductionSummaryView view = selfIntroductionPage.getContent().get(i);
                Assertions.assertThat(view.yearOfBirth()).isGreaterThanOrEqualTo(fromYearOfBirth);
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
            Page<SelfIntroductionSummaryView> selfIntroductionPage = selfIntroductionQueryRepository
                    .findSelfIntroductions(searchCondition, PageRequest.of(0, 20));

            // Then
            Assertions.assertThat(selfIntroductionPage).isNotNull();
            for (int i = 0; i < selfIntroductionPage.getTotalElements(); i++) {
                SelfIntroductionSummaryView view = selfIntroductionPage.getContent().get(i);
                Assertions.assertThat(view.yearOfBirth()).isBetween(fromYearOfBirth, toYearOfBirth);
            }
        }

        @Test
        @DisplayName("검색 조건 중 지역을 명시한 경우, 해당 지역 출신의 멤벅 작성한 글을 불러온다.")
        void getSelfIntroductionByRegionCondition() {
            // Given
            Region region = maleMember.getProfile().getRegion();
            SelfIntroductionSearchCondition searchCondition = new SelfIntroductionSearchCondition(
                    List.of(region), null, null, null
            );

            List<SelfIntroduction> maleSelfIntroduction = selfIntroductions.stream().filter(s -> s.getMemberId() == maleMember.getId()).toList();

            // When
            Page<SelfIntroductionSummaryView> selfIntroductionPage = selfIntroductionQueryRepository
                    .findSelfIntroductions(searchCondition, PageRequest.of(0, 20));

            // Then
            Assertions.assertThat(selfIntroductionPage).isNotNull();
            for (int i = 0; i < selfIntroductionPage.getTotalElements(); i++) {
                SelfIntroductionSummaryView view = selfIntroductionPage.getContent().get(i);
                SelfIntroduction selfIntroduction = maleSelfIntroduction.get(i);

                Assertions.assertThat(view.id()).isEqualTo(selfIntroduction.getId());
            }
        }

        @Test
        @DisplayName("모든 조건을 설정해 글을 조회합니다.")
        void findSelfIntroductions() {
            // Given
            SelfIntroductionSearchCondition searchCondition = new SelfIntroductionSearchCondition(
                    List.of(Region.DAEJEON, Region.SEOUL), femaleMember.getProfile().getYearOfBirth().getValue(), maleMember.getProfile().getYearOfBirth().getValue(), maleMember.getProfile().getGender()
            );
            List<SelfIntroduction> maleSelfIntroduction = selfIntroductions.stream().filter(s -> s.getMemberId() == maleMember.getId()).toList();

            // When
            Page<SelfIntroductionSummaryView> selfIntroductionPage = selfIntroductionQueryRepository
                    .findSelfIntroductions(searchCondition, PageRequest.of(0, 20));

            // Then
            Assertions.assertThat(selfIntroductionPage).isNotNull();
            for (int i = 0; i < selfIntroductionPage.getTotalElements(); i++) {
                SelfIntroductionSummaryView view = selfIntroductionPage.getContent().get(i);
                SelfIntroduction selfIntroduction = maleSelfIntroduction.get(i);

                Assertions.assertThat(view.id()).isEqualTo(selfIntroduction.getId());
            }
        }
    }
}
