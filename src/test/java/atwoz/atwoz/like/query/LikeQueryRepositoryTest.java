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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDslConfig.class, LikeQueryRepository.class})
class LikeQueryRepositoryTest {
    private static final int NUMBER_OF_PEOPLE = 30;
    private static final int PAGE_SIZE = 12;

    @Autowired
    private LikeQueryRepository likeQueryRepository;
    @Autowired
    private TestEntityManager em;

    private List<Member> senders;
    private List<ProfileImage> senderProfileImages;
    private List<Member> receivers;
    private List<ProfileImage> receiverProfileImages;
    private List<Like> likes;

    @BeforeEach
    void setUp() {
        createSenders();
        createSenderProfileImages();

        createReceivers();
        createReceiverProfileImages();

        createSenderToReceiverLikes();
        createReceiverToSenderLikes();

        em.flush();
        em.clear();

        likes.sort(Comparator.comparing(Like::getId).reversed());
    }

    @Test
    @DisplayName("보낸 좋아요 목록을 조회한다.")
    void findSentLikes() {
        // given
        var senderId = senders.getFirst().getId();
        var expectedLikes = likes.stream()
            .filter(like -> like.getSenderId().equals(senderId))
            .sorted(Comparator.comparing(Like::getId).reversed())
            .limit(PAGE_SIZE)
            .toList();

        var profileImageUrlMap = receiverProfileImages.stream()
            .collect(Collectors.toMap(ProfileImage::getMemberId, ProfileImage::getUrl));
        var receiverProfileMap = receivers.stream()
            .collect(Collectors.toMap(Member::getId, Member::getProfile));

        // when
        var firstPage = likeQueryRepository.findSentLikes(senderId, null);

        // then
        assertThat(firstPage).hasSize(expectedLikes.size());

        for (int i = 0; i < expectedLikes.size(); i++) {
            var likeView = firstPage.get(i);
            var expectedLike = expectedLikes.get(i);

            assertThat(likeView.likeId()).isEqualTo(expectedLike.getId());
            assertThat(likeView.opponentId()).isEqualTo(expectedLike.getReceiverId());

            var expectedUrl = profileImageUrlMap.get(expectedLike.getReceiverId());
            assertThat(likeView.profileImageUrl()).isEqualTo(expectedUrl);

            var receiverProfile = receiverProfileMap.get(expectedLike.getReceiverId());
            assertThat(likeView.nickname()).isEqualTo(receiverProfile.getNickname().getValue());
            assertThat(likeView.city()).isEqualTo(receiverProfile.getRegion().getCity().toString());
            assertThat(likeView.yearOfBirth()).isEqualTo(receiverProfile.getYearOfBirth().getValue());

            boolean expectedMutual = expectedLike.getSenderId() % 2 != 0;
            assertThat(likeView.isMutualLike()).isEqualTo(expectedMutual);
        }
    }

    @Test
    @DisplayName("받은 좋아요 목록을 조회한다.")
    void findReceivedLikes() {
        // given
        var receiverId = receivers.getFirst().getId();
        var expectedLikes = likes.stream()
            .filter(like -> like.getReceiverId().equals(receiverId))
            .sorted(Comparator.comparing(Like::getId).reversed())
            .limit(PAGE_SIZE)
            .toList();

        var profileImageUrlMap = senderProfileImages.stream()
            .collect(Collectors.toMap(ProfileImage::getMemberId, ProfileImage::getUrl));
        var senderProfileMap = senders.stream()
            .collect(Collectors.toMap(Member::getId, Member::getProfile));

        // when
        var firstPage = likeQueryRepository.findReceivedLikes(receiverId, null);

        // then
        assertThat(firstPage).hasSize(expectedLikes.size());

        for (int i = 0; i < expectedLikes.size(); i++) {
            var likeView = firstPage.get(i);
            var expectedLike = expectedLikes.get(i);

            assertThat(likeView.likeId()).isEqualTo(expectedLike.getId());
            assertThat(likeView.opponentId()).isEqualTo(expectedLike.getSenderId());

            var expectedUrl = profileImageUrlMap.get(expectedLike.getSenderId());
            assertThat(likeView.profileImageUrl()).isEqualTo(expectedUrl);

            var senderProfile = senderProfileMap.get(expectedLike.getSenderId());
            assertThat(likeView.nickname()).isEqualTo(senderProfile.getNickname().getValue());
            assertThat(likeView.city()).isEqualTo(senderProfile.getRegion().getCity().toString());
            assertThat(likeView.yearOfBirth()).isEqualTo(senderProfile.getYearOfBirth().getValue());

            boolean expectedMutual = expectedLike.getSenderId() % 2 != 0;
            assertThat(likeView.isMutualLike()).isEqualTo(expectedMutual);
        }
    }

    private void createSenders() {
        senders = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PEOPLE; i++) {
            var sender = createMember(String.format("010%04d1234", i), "sender" + i, District.GWANAK_GU);
            senders.add(sender);
            em.persist(sender);
        }
    }

    private void createSenderProfileImages() {
        senderProfileImages = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PEOPLE; i++) {
            if (i % 2 == 0) {
                continue;
            }
            var senderImage = createProfileImage(senders.get(i).getId(), "/sender" + i + ".jpg");
            senderProfileImages.add(senderImage);
            em.persist(senderImage);
        }
    }


    private void createReceivers() {
        receivers = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PEOPLE; i++) {
            var receiver = createMember(String.format("0101234%04d", i), "receiver" + i, District.GANGNAM_GU);
            receivers.add(receiver);
            em.persist(receiver);
        }
    }

    private void createReceiverProfileImages() {
        receiverProfileImages = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PEOPLE; i++) {
            if (i % 2 == 1) {
                continue;
            }
            var receiverImage = createProfileImage(receivers.get(i).getId(), "/receiver" + i + ".jpg");
            receiverProfileImages.add(receiverImage);
            em.persist(receiverImage);
        }
    }

    private void createSenderToReceiverLikes() {
        likes = new ArrayList<>();
        for (final Member sender : senders) {
            for (final Member receiver : receivers) {
                var like = Like.of(sender.getId(), receiver.getId(), LikeLevel.INTERESTED);
                likes.add(like);
                em.persist(like);
            }
        }
    }

    private void createReceiverToSenderLikes() {
        for (final Member receiver : receivers) {
            for (final Member sender : senders) {
                if (sender.getId() % 2 == 0) {
                    continue;
                }
                var like = Like.of(receiver.getId(), sender.getId(), LikeLevel.HIGHLY_INTERESTED);
                likes.add(like);
                em.persist(like);
            }
        }
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

    private ProfileImage createProfileImage(long memberId, String url) {
        return ProfileImage.builder()
            .memberId(memberId)
            .imageUrl(ImageUrl.from(url))
            .isPrimary(true)
            .order(1)
            .build();
    }
}