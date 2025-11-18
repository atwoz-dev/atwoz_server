package atwoz.atwoz.match.query;

import atwoz.atwoz.common.MockEventsExtension;
import atwoz.atwoz.common.config.QueryDslConfig;
import atwoz.atwoz.match.command.domain.match.Match;
import atwoz.atwoz.match.command.domain.match.MatchContactType;
import atwoz.atwoz.match.command.domain.match.MatchType;
import atwoz.atwoz.match.command.domain.match.vo.Message;
import atwoz.atwoz.member.command.domain.member.District;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
import atwoz.atwoz.member.command.domain.member.vo.Region;
import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import atwoz.atwoz.member.command.domain.profileImage.vo.ImageUrl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@Import({QueryDslConfig.class, MatchQueryRepository.class})
@ExtendWith(MockEventsExtension.class)
public class MatchQueryRepositoryTest {
    @Autowired
    private MatchQueryRepository matchQueryRepository;

    @Autowired
    private TestEntityManager em;

    private List<Member> members;
    private List<ProfileImage> memberProfileImages;

    @BeforeEach
    void setUp() {
        createMembers();
        createProfileImages();
    }

    @Test
    @DisplayName("보낸 매칭 메세지를 조회한다.")
    void findSentMatches() {
        // given
        Member targetMember = members.get(0);

        for (int i = 0; i < 10; i++) {
            Member responder = members.get(i);
            if (responder.getId().equals(targetMember.getId())) {
                continue;
            }
            Match match = Match
                .request(targetMember.getId(), responder.getId(), Message.from("Hello"), "알림용이름", MatchType.MATCH,
                    MatchContactType.PHONE_NUMBER);

            if (i % 2 == 0) {
                match.approve(Message.from("Hi"), "알림용이름", MatchContactType.PHONE_NUMBER);
            }
            em.persist(match);
        }

        // When
        List<MatchView> matchViews = matchQueryRepository.findSentMatches(targetMember.getId(), null);

        // Then
        Assertions.assertThat(matchViews.size()).isEqualTo(9);

        for (int i = 0; i < matchViews.size(); i++) {
            MatchView matchView = matchViews.get(i);
            Long opponentId = matchView.opponentId();
            Member member = members.stream().filter(m -> m.getId().equals(opponentId)).findFirst().orElse(null);
            ProfileImage profileImage = memberProfileImages.stream()
                .filter(p -> p.getMemberId().equals(opponentId))
                .findFirst()
                .orElse(null);
            Assertions.assertThat(member).isNotNull();
            Assertions.assertThat(profileImage).isNotNull();
            Assertions.assertThat(matchView.profileImageUrl()).isEqualTo(profileImage.getUrl());
        }
    }

    @Test
    @DisplayName("받은 매칭 메세지를 조회한다.")
    void findReceiveMatches() {
        // given
        Member targetMember = members.get(0);

        for (int i = 0; i < 10; i++) {
            Member requester = members.get(i);
            if (requester.getId().equals(targetMember.getId())) {
                continue;
            }
            Match match = Match
                .request(requester.getId(), targetMember.getId(), Message.from("Hello"), "알림용이름", MatchType.MATCH,
                    MatchContactType.PHONE_NUMBER);

            if (i % 2 == 0) {
                match.approve(Message.from("Hi"), "알림용이름", MatchContactType.PHONE_NUMBER);
            }
            em.persist(match);
        }

        // When
        List<MatchView> matchViews = matchQueryRepository.findReceiveMatches(targetMember.getId(), null);

        // Then
        Assertions.assertThat(matchViews.size()).isEqualTo(9);

        for (int i = 0; i < matchViews.size(); i++) {
            MatchView matchView = matchViews.get(i);
            Long opponentId = matchView.opponentId();
            Member member = members.stream().filter(m -> m.getId().equals(opponentId)).findFirst().orElse(null);
            ProfileImage profileImage = memberProfileImages.stream()
                .filter(p -> p.getMemberId().equals(opponentId))
                .findFirst()
                .orElse(null);
            Assertions.assertThat(member).isNotNull();
            Assertions.assertThat(profileImage).isNotNull();
            Assertions.assertThat(matchView.profileImageUrl()).isEqualTo(profileImage.getUrl());
        }
    }


    private void createMembers() {
        members = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Member member = createMember("0101234567" + i, "member" + i, District.ANDONG_SI);
            members.add(member);
        }
    }

    private void createProfileImages() {
        memberProfileImages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ProfileImage profileImage = createProfileImage(members.get(i).getId(), "profileImage" + i);
            memberProfileImages.add(profileImage);
            em.persist(profileImage);
        }
    }

    private Member createMember(String phone, String nickname, District district) {
        var member = Member.fromPhoneNumber(phone);
        em.persist(member);
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
