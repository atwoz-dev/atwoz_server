package atwoz.atwoz.like.query;

import atwoz.atwoz.common.config.QueryDslConfig;
import atwoz.atwoz.like.command.domain.Like;
import atwoz.atwoz.like.command.domain.LikeLevel;
import atwoz.atwoz.member.command.domain.member.District;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
import atwoz.atwoz.member.command.domain.member.vo.Region;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDslConfig.class, LikeQueryRepository.class})
class LikeQueryRepositoryTest {
    private static final int PAGE_SIZE = 12;

    @Autowired
    private LikeQueryRepository likeQueryRepository;
    @Autowired
    private TestEntityManager em;

    private Member sender;
    private List<Member> receivers;
    private ProfileImage senderImage;
    private List<ProfileImage> receiverImages;
    private List<Like> likes;

    @BeforeEach
    void setUp() {
        sender = createMember("01012345678", "sender", District.GANGBUK_GU);
        em.persistAndFlush(sender);

        receivers = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            var receiver = createMember(String.format("010%08d", i), "receiver" + i, District.DONG_GU_DAEJEON);
            receivers.add(receiver);
            em.persist(receiver);
        }
        em.flush();

        senderImage = createProfileImage(sender.getId(), "/sender.jpg");
        em.persistAndFlush(senderImage);

        receiverImages = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            var receiverImage = createProfileImage(receivers.get(i).getId(), "/receiver" + i + ".jpg");
            receiverImages.add(receiverImage);
            em.persist(receiverImage);
        }
        em.flush();

        likes = new ArrayList<>();
        for (final Member receiver : receivers) {
            var like = Like.of(sender.getId(), receiver.getId(), LikeLevel.HIGHLY_INTERESTED);
            likes.add(like);
            em.persist(like);
        }
        em.flush();
        em.clear();

        likes.sort((l1, l2) -> Math.toIntExact(l2.getId() - l1.getId()));
    }

    @Test
    @DisplayName("보낸 좋아요 목록을 최초 조회한다.")
    void findSentLikes() {
        // when
        var result = likeQueryRepository.findSentLikes(sender.getId(), null);

        // then
        assertThat(result).hasSize(PAGE_SIZE);

        for (int i = 0; i < result.size(); i++) {
            var likeView = result.get(i);
            var like = likes.get(i);
            var receiverProfile = receivers.get(receivers.size() - 1 - i).getProfile();
            var receiverImage = receiverImages.get(receivers.size() - 1 - i);

            assertThat(likeView.id()).isEqualTo(like.getId());
            assertThat(likeView.profileImageUrl()).isEqualTo(receiverImage.getUrl());
            assertThat(likeView.nickname()).isEqualTo(receiverProfile.getNickname().getValue());
            assertThat(likeView.city()).isEqualTo(receiverProfile.getRegion().getCity().toString());
            assertThat(likeView.yearOfBirth()).isEqualTo(receiverProfile.getYearOfBirth().getValue());
        }
    }

    @Test
    @DisplayName("보낸 좋아요 목록을 no offset paging으로 조회한다.")
    void findSentLikesWithNoOffsetPaging() {
        // when
        List<LikeView> firstPage = likeQueryRepository.findSentLikes(sender.getId(), null);
        List<LikeView> secondPage = likeQueryRepository.findSentLikes(sender.getId(), firstPage.getLast().id());
        List<LikeView> thirdPage = likeQueryRepository.findSentLikes(sender.getId(), secondPage.getLast().id());

        // then
        assertThat(firstPage).hasSize(PAGE_SIZE);
        assertThat(secondPage).hasSize(PAGE_SIZE);
        assertThat(thirdPage).hasSize(1);

        assertThat(firstPage.getFirst().id()).isGreaterThan(firstPage.getLast().id());
        assertThat(secondPage.getFirst().id()).isGreaterThan(secondPage.getLast().id());

        assertThat(firstPage.getLast().id()).isGreaterThan(secondPage.getFirst().id());
        assertThat(secondPage.getLast().id()).isGreaterThan(thirdPage.getFirst().id());
    }


    @Test
    @DisplayName("받은 좋아요 목록을 최초 조회한다.")
    void findReceivedLikes() {
        // given
        var receiver = receivers.getFirst();

        // when
        var result = likeQueryRepository.findReceivedLikes(receiver.getId(), null);

        // then
        assertThat(result).hasSize(1);

        var likeView = result.getFirst();
        assertThat(likeView.id()).isEqualTo(likes.getLast().getId());
        assertThat(likeView.profileImageUrl()).isEqualTo(senderImage.getUrl());
        assertThat(likeView.nickname()).isEqualTo(sender.getProfile().getNickname().getValue());
        assertThat(likeView.city()).isEqualTo(sender.getProfile().getRegion().getCity().toString());
        assertThat(likeView.yearOfBirth()).isEqualTo(sender.getProfile().getYearOfBirth().getValue());
    }

    private Member createMember(String phone, String nickname, District district) {
        var member = Member.fromPhoneNumber(phone);
        var profile = MemberProfile.builder()
            .nickname(Nickname.from(nickname))
            .region(Region.of(district))
            .yearOfBirth(1995)
            .build();
        member.updateProfile(profile);
        return member;
    }

    private ProfileImage createProfileImage(Long memberId, String url) {
        return ProfileImage.builder()
            .memberId(memberId)
            .imageUrl(ImageUrl.from(url))
            .isPrimary(true)
            .order(1)
            .build();
    }
}