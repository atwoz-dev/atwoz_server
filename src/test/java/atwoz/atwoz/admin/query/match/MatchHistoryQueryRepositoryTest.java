package atwoz.atwoz.admin.query.match;

import atwoz.atwoz.QuerydslConfig;
import atwoz.atwoz.common.MockEventsExtension;
import atwoz.atwoz.match.command.domain.match.Match;
import atwoz.atwoz.match.command.domain.match.MatchContactType;
import atwoz.atwoz.match.command.domain.match.MatchType;
import atwoz.atwoz.match.command.domain.match.vo.Message;
import atwoz.atwoz.member.command.domain.member.Gender;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
import atwoz.atwoz.member.command.domain.member.vo.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, MatchHistoryQueryRepository.class})
@ExtendWith(MockEventsExtension.class)
class MatchHistoryQueryRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private MatchHistoryQueryRepository matchHistoryQueryRepository;

    @Test
    @DisplayName("조건 없이 전체 매치 내역을 조회합니다.")
    void findAllMatchHistories() {
        // given
        Member requester = createMember("요청자", "01011111111");
        Member responder = createMember("응답자", "01022222222");
        em.persist(requester);
        em.persist(responder);

        Match match1 = createMatch(requester, responder, "안녕하세요");
        Match match2 = createMatch(responder, requester, "반갑습니다");
        em.persist(match1);
        em.persist(match2);

        em.flush();
        em.clear();

        var condition = new MatchHistorySearchCondition(null, null, null, null, null);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<MatchHistoryView> matchHistories = matchHistoryQueryRepository.findMatchHistories(condition, pageRequest);

        // then
        assertThat(matchHistories.getTotalElements()).isEqualTo(2);
        assertThat(matchHistories.getContent()).extracting("requesterNickname")
            .containsExactlyInAnyOrder("요청자", "응답자");
    }

    @Test
    @DisplayName("매치 상태로 매치 내역을 조회합니다.")
    void findMatchHistoriesByStatus() {
        // given
        Member requester = createMember("요청자", "01011111111");
        Member responder = createMember("응답자", "01022222222");
        em.persist(requester);
        em.persist(responder);

        Match waitingMatch = createMatch(requester, responder, "대기 중");
        Match matchedMatch = createMatch(responder, requester, "매칭됨");
        matchedMatch.approve(Message.from("수락합니다"), "요청자", MatchContactType.KAKAO);

        em.persist(waitingMatch);
        em.persist(matchedMatch);

        em.flush();
        em.clear();

        var condition = new MatchHistorySearchCondition("WAITING", null, null, null, null);
        var pageRequest = PageRequest.of(0, 10);

        // when
        var matchHistories = matchHistoryQueryRepository.findMatchHistories(condition, pageRequest);

        // then
        assertThat(matchHistories.getTotalElements()).isEqualTo(1);
        assertThat(matchHistories.getContent().getFirst().status()).isEqualTo("WAITING");
        assertThat(matchHistories.getContent().getFirst().requestMessage()).isEqualTo("대기 중");
    }

    @Test
    @DisplayName("닉네임으로 매치 내역을 조회합니다 - 요청자 닉네임으로 검색")
    void findMatchHistoriesByRequesterNickname() {
        // given
        Member requester = createMember("요청자", "01011111111");
        Member responder = createMember("응답자", "01022222222");
        Member other = createMember("다른사람", "01033333333");
        em.persist(requester);
        em.persist(responder);
        em.persist(other);

        Match match1 = createMatch(requester, responder, "안녕하세요");
        Match match2 = createMatch(other, responder, "반갑습니다");
        em.persist(match1);
        em.persist(match2);

        em.flush();
        em.clear();

        var condition = new MatchHistorySearchCondition(null, "요청자", null, null, null);
        var pageRequest = PageRequest.of(0, 10);

        // when
        var matchHistories = matchHistoryQueryRepository.findMatchHistories(condition, pageRequest);

        // then
        assertThat(matchHistories.getTotalElements()).isEqualTo(1);
        assertThat(matchHistories.getContent().getFirst().requesterNickname()).isEqualTo("요청자");
        assertThat(matchHistories.getContent().getFirst().responderNickname()).isEqualTo("응답자");
    }

    @Test
    @DisplayName("닉네임으로 매치 내역을 조회합니다 - 응답자 닉네임으로 검색")
    void findMatchHistoriesByResponderNickname() {
        // given
        Member requester = createMember("요청자", "01011111111");
        Member responder = createMember("응답자", "01022222222");
        Member other = createMember("다른사람", "01033333333");
        em.persist(requester);
        em.persist(responder);
        em.persist(other);

        Match match1 = createMatch(requester, responder, "안녕하세요");
        Match match2 = createMatch(requester, other, "반갑습니다");
        em.persist(match1);
        em.persist(match2);

        em.flush();
        em.clear();

        var condition = new MatchHistorySearchCondition(null, "응답자", null, null, null);
        var pageRequest = PageRequest.of(0, 10);

        // when
        var matchHistories = matchHistoryQueryRepository.findMatchHistories(condition, pageRequest);

        // then
        assertThat(matchHistories.getTotalElements()).isEqualTo(1);
        assertThat(matchHistories.getContent().getFirst().requesterNickname()).isEqualTo("요청자");
        assertThat(matchHistories.getContent().getFirst().responderNickname()).isEqualTo("응답자");
    }

    @Test
    @DisplayName("전화번호로 매치 내역을 조회합니다 - 요청자 전화번호로 검색")
    void findMatchHistoriesByRequesterPhoneNumber() {
        // given
        Member requester = createMember("요청자", "01011111111");
        Member responder = createMember("응답자", "01022222222");
        Member other = createMember("다른사람", "01033333333");
        em.persist(requester);
        em.persist(responder);
        em.persist(other);

        Match match1 = createMatch(requester, responder, "안녕하세요");
        Match match2 = createMatch(other, responder, "반갑습니다");
        em.persist(match1);
        em.persist(match2);

        em.flush();
        em.clear();

        var condition = new MatchHistorySearchCondition(null, null, "01011111111", null, null);
        var pageRequest = PageRequest.of(0, 10);

        // when
        var matchHistories = matchHistoryQueryRepository.findMatchHistories(condition, pageRequest);

        // then
        assertThat(matchHistories.getTotalElements()).isEqualTo(1);
        assertThat(matchHistories.getContent().getFirst().requesterNickname()).isEqualTo("요청자");
    }

    @Test
    @DisplayName("전화번호로 매치 내역을 조회합니다 - 응답자 전화번호로 검색")
    void findMatchHistoriesByResponderPhoneNumber() {
        // given
        Member requester = createMember("요청자", "01011111111");
        Member responder = createMember("응답자", "01022222222");
        Member other = createMember("다른사람", "01033333333");
        em.persist(requester);
        em.persist(responder);
        em.persist(other);

        Match match1 = createMatch(requester, responder, "안녕하세요");
        Match match2 = createMatch(requester, other, "반갑습니다");
        em.persist(match1);
        em.persist(match2);

        em.flush();
        em.clear();

        var condition = new MatchHistorySearchCondition(null, null, "01022222222", null, null);
        var pageRequest = PageRequest.of(0, 10);

        // when
        var matchHistories = matchHistoryQueryRepository.findMatchHistories(condition, pageRequest);

        // then
        assertThat(matchHistories.getTotalElements()).isEqualTo(1);
        assertThat(matchHistories.getContent().getFirst().responderNickname()).isEqualTo("응답자");
    }

    @Test
    @DisplayName("시작일과 종료일로 매치 내역을 조회합니다.")
    void findMatchHistoriesByDateRange() {
        // given
        Member requester = createMember("요청자", "01011111111");
        Member responder = createMember("응답자", "01022222222");
        em.persist(requester);
        em.persist(responder);

        Match match = createMatch(requester, responder, "안녕하세요");
        em.persist(match);

        em.flush();
        em.clear();

        var condition = new MatchHistorySearchCondition(null, null, null, LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1));
        var pageRequest = PageRequest.of(0, 10);

        // when
        var matchHistories = matchHistoryQueryRepository.findMatchHistories(condition, pageRequest);

        // then
        assertThat(matchHistories.getTotalElements()).isEqualTo(1);
        assertThat(matchHistories.getContent().getFirst().requesterNickname()).isEqualTo("요청자");
    }

    @Test
    @DisplayName("모든 조건으로 매치 내역을 조회합니다.")
    void findMatchHistoriesWithAllConditions() {
        // given
        Member requester = createMember("요청자", "01011111111");
        Member responder = createMember("응답자", "01022222222");
        em.persist(requester);
        em.persist(responder);

        Match match = createMatch(requester, responder, "안녕하세요");
        em.persist(match);

        em.flush();
        em.clear();

        var condition = new MatchHistorySearchCondition("WAITING", "요청자", "01011111111", LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1));
        var pageRequest = PageRequest.of(0, 10);

        // when
        var matchHistories = matchHistoryQueryRepository.findMatchHistories(condition, pageRequest);

        // then
        assertThat(matchHistories.getTotalElements()).isEqualTo(1);
        assertThat(matchHistories.getContent().getFirst().status()).isEqualTo("WAITING");
        assertThat(matchHistories.getContent().getFirst().requesterNickname()).isEqualTo("요청자");
        assertThat(matchHistories.getContent().getFirst().responderNickname()).isEqualTo("응답자");
        assertThat(matchHistories.getContent().getFirst().requestMessage()).isEqualTo("안녕하세요");
    }

    @Test
    @DisplayName("매치 응답 메시지와 읽은 시간을 포함하여 조회합니다.")
    void findMatchHistoriesWithResponseMessage() {
        // given
        Member requester = createMember("요청자", "01011111111");
        Member responder = createMember("응답자", "01022222222");
        em.persist(requester);
        em.persist(responder);

        Match match = createMatch(requester, responder, "안녕하세요");
        match.read(responder.getId());
        match.approve(Message.from("반갑습니다"), "응답자", MatchContactType.KAKAO);
        em.persist(match);

        em.flush();
        em.clear();

        var condition = new MatchHistorySearchCondition("MATCHED", null, null, null, null);
        var pageRequest = PageRequest.of(0, 10);

        // when
        var matchHistories = matchHistoryQueryRepository.findMatchHistories(condition, pageRequest);

        // then
        assertThat(matchHistories.getTotalElements()).isEqualTo(1);
        var result = matchHistories.getContent().getFirst();
        assertThat(result.status()).isEqualTo("MATCHED");
        assertThat(result.requestMessage()).isEqualTo("안녕하세요");
        assertThat(result.responseMessage()).isEqualTo("반갑습니다");
        assertThat(result.readByResponderAt()).isNotNull();
    }

    private Member createMember(String nickname, String phoneNumber) {
        return Member.builder()
            .phoneNumber(PhoneNumber.from(phoneNumber))
            .profile(
                MemberProfile.builder()
                    .nickname(Nickname.from(nickname))
                    .gender(Gender.MALE)
                    .build()
            )
            .build();
    }

    private Match createMatch(Member requester, Member responder, String requestMessage) {
        return Match.request(
            requester.getId(),
            responder.getId(),
            Message.from(requestMessage),
            requester.getProfile().getNickname().getValue(),
            MatchType.MATCH,
            MatchContactType.KAKAO
        );
    }
}