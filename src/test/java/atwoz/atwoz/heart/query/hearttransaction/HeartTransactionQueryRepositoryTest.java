package atwoz.atwoz.heart.query.hearttransaction;

import atwoz.atwoz.common.config.QueryDslConfig;
import atwoz.atwoz.heart.command.domain.hearttransaction.HeartTransaction;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartAmount;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartBalance;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionType;
import atwoz.atwoz.heart.query.hearttransaction.condition.HeartTransactionSearchCondition;
import atwoz.atwoz.heart.query.hearttransaction.view.HeartTransactionView;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.vo.MemberProfile;
import atwoz.atwoz.member.command.domain.member.vo.Nickname;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DataJpaTest
@Import({QueryDslConfig.class, HeartTransactionQueryRepository.class})
class HeartTransactionQueryRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private HeartTransactionQueryRepository heartTransactionQueryRepository;

    @Test
    @DisplayName("하트 거래 내역 페이지 조회 파라미터 순서 테스트")
    void findPage() {
        // given
        HeartTransactionSearchCondition condition = new HeartTransactionSearchCondition(
            null,
            null,
            null,
            null
        );
        PageRequest pageRequest = PageRequest.of(0, 10);

        HeartTransaction heartTransaction1 = createHeartTransaction(1L);
        entityManager.persist(heartTransaction1);
        entityManager.flush();

        // when
        final Page<HeartTransactionView> result = heartTransactionQueryRepository.findPage(condition,
            pageRequest);

        // then
        assertThat(result).isNotNull();
        List<HeartTransactionView> heartTransactionViews = result.getContent();
        assertThat(heartTransactionViews).hasSize(1);
        HeartTransactionView heartTransactionView = heartTransactionViews.get(0);
        assertThat(heartTransactionView.id()).isEqualTo(heartTransaction1.getId());
        assertThat(heartTransactionView.transactionType()).isEqualTo(heartTransaction1.getTransactionType().name());
        assertThat(heartTransactionView.content()).isEqualTo(heartTransaction1.getContent());
        assertThat(heartTransactionView.heartAmount()).isEqualTo(heartTransaction1.getHeartAmount().getAmount());
        Long heartBalance = heartTransaction1.getHeartBalance().getMissionHeartBalance() +
            heartTransaction1.getHeartBalance().getPurchaseHeartBalance();
        assertThat(heartTransactionView.heartBalance()).isEqualTo(heartBalance);
        assertThat(heartTransactionView.createdAt()).isCloseTo(heartTransaction1.getCreatedAt(),
            within(1, ChronoUnit.MICROS));
    }

    @Test
    @DisplayName("하트 거래 내역 페이지 조회 nickname condition 테스트")
    void findPageWithNicknameCondition() {
        // given
        HeartTransactionSearchCondition condition = new HeartTransactionSearchCondition(
            "name1",
            null,
            null,
            null
        );
        PageRequest pageRequest = PageRequest.of(0, 10);

        Member member1 = createMember("01000000000", "nickname1");
        Member member2 = createMember("01011111111", "nickname2");

        entityManager.persist(member1);
        entityManager.persist(member2);
        entityManager.flush();

        HeartTransaction heartTransaction1 = createHeartTransaction(member1.getId());
        HeartTransaction heartTransaction2 = createHeartTransaction(member2.getId());

        entityManager.persist(heartTransaction1);
        entityManager.persist(heartTransaction2);
        entityManager.flush();

        // when
        final Page<HeartTransactionView> result = heartTransactionQueryRepository.findPage(condition,
            pageRequest);

        // then
        assertThat(result).isNotNull();
        List<HeartTransactionView> heartTransactionViews = result.getContent();
        assertThat(heartTransactionViews).hasSize(1);
        HeartTransactionView heartTransactionView = heartTransactionViews.get(0);
        assertThat(heartTransactionView.id()).isEqualTo(heartTransaction1.getId());
    }

    @Test
    @DisplayName("하트 거래 내역 페이지 조회 phoneNumber condition 테스트")
    void findPageWithPhoneNumberCondition() {
        // given
        HeartTransactionSearchCondition condition = new HeartTransactionSearchCondition(
            null,
            "01000000000",
            null,
            null
        );
        PageRequest pageRequest = PageRequest.of(0, 10);

        Member member1 = createMember("01000000000", "nickname1");
        Member member2 = createMember("01011111111", "nickname2");

        entityManager.persist(member1);
        entityManager.persist(member2);
        entityManager.flush();

        HeartTransaction heartTransaction1 = createHeartTransaction(member1.getId());
        HeartTransaction heartTransaction2 = createHeartTransaction(member2.getId());

        entityManager.persist(heartTransaction1);
        entityManager.persist(heartTransaction2);
        entityManager.flush();

        // when
        final Page<HeartTransactionView> result = heartTransactionQueryRepository.findPage(condition,
            pageRequest);

        // then
        assertThat(result).isNotNull();
        List<HeartTransactionView> heartTransactionViews = result.getContent();
        assertThat(heartTransactionViews).hasSize(1);
        HeartTransactionView heartTransactionView = heartTransactionViews.get(0);
        assertThat(heartTransactionView.id()).isEqualTo(heartTransaction1.getId());
    }

    @Test
    @DisplayName("하트 거래 내역 페이지 조회 nickname and phoneNumber condition 테스트")
    void findPageWithNicknameAndPhoneNumberCondition() {
        // given
        HeartTransactionSearchCondition condition1 = new HeartTransactionSearchCondition(
            "name1",
            "01000000000",
            null,
            null
        );
        HeartTransactionSearchCondition condition2 = new HeartTransactionSearchCondition(
            "name1",
            "01011111111",
            null,
            null
        );
        PageRequest pageRequest = PageRequest.of(0, 10);

        Member member1 = createMember("01000000000", "nickname1");
        Member member2 = createMember("01011111111", "nickname2");

        entityManager.persist(member1);
        entityManager.persist(member2);
        entityManager.flush();

        HeartTransaction heartTransaction1 = createHeartTransaction(member1.getId());
        HeartTransaction heartTransaction2 = createHeartTransaction(member2.getId());

        entityManager.persist(heartTransaction1);
        entityManager.persist(heartTransaction2);
        entityManager.flush();

        // when
        final Page<HeartTransactionView> result1 = heartTransactionQueryRepository.findPage(condition1,
            pageRequest);
        final Page<HeartTransactionView> result2 = heartTransactionQueryRepository.findPage(condition2,
            pageRequest);

        // then
        assertThat(result1).isNotNull();
        List<HeartTransactionView> heartTransactionViews = result1.getContent();
        assertThat(heartTransactionViews).hasSize(1);
        HeartTransactionView heartTransactionView = heartTransactionViews.get(0);
        assertThat(heartTransactionView.id()).isEqualTo(heartTransaction1.getId());

        assertThat(result2).isEmpty();
    }

    @Test
    @DisplayName("하트 거래 내역 페이지 조회 createdDateGoe condition 테스트")
    void findPageWithCreatedDateGoeCondition() {
        // given
        HeartTransaction heartTransaction1 = createHeartTransaction(1L);
        entityManager.persist(heartTransaction1);
        entityManager.flush();

        HeartTransactionSearchCondition condition1 = new HeartTransactionSearchCondition(
            null,
            null,
            heartTransaction1.getCreatedAt().toLocalDate(),
            null
        );

        HeartTransactionSearchCondition condition2 = new HeartTransactionSearchCondition(
            null,
            null,
            heartTransaction1.getCreatedAt().toLocalDate().plusDays(1),
            null
        );
        PageRequest pageRequest = PageRequest.of(0, 10);


        // when
        final Page<HeartTransactionView> result1 = heartTransactionQueryRepository.findPage(condition1,
            pageRequest);

        final Page<HeartTransactionView> result2 = heartTransactionQueryRepository.findPage(condition2,
            pageRequest);

        // then
        assertThat(result1).isNotNull();
        List<HeartTransactionView> heartTransactionViews1 = result1.getContent();
        assertThat(heartTransactionViews1).hasSize(1);
        HeartTransactionView heartTransactionView1 = heartTransactionViews1.get(0);
        assertThat(heartTransactionView1.id()).isEqualTo(heartTransaction1.getId());

        assertThat(result2).isEmpty();
    }

    @Test
    @DisplayName("하트 거래 내역 페이지 조회 createdDateLoe condition 테스트")
    void findPageWithCreatedDateLoeCondition() {
        // given
        HeartTransaction heartTransaction1 = createHeartTransaction(1L);
        entityManager.persist(heartTransaction1);
        entityManager.flush();

        HeartTransactionSearchCondition condition1 = new HeartTransactionSearchCondition(
            null,
            null,
            null,
            heartTransaction1.getCreatedAt().toLocalDate()
        );

        HeartTransactionSearchCondition condition2 = new HeartTransactionSearchCondition(
            null,
            null,
            null,
            heartTransaction1.getCreatedAt().toLocalDate().minusDays(1)
        );
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        final Page<HeartTransactionView> result1 = heartTransactionQueryRepository.findPage(condition1,
            pageRequest);

        final Page<HeartTransactionView> result2 = heartTransactionQueryRepository.findPage(condition2,
            pageRequest);

        // then
        assertThat(result1).isNotNull();
        List<HeartTransactionView> heartTransactionViews1 = result1.getContent();
        assertThat(heartTransactionViews1).hasSize(1);
        HeartTransactionView heartTransactionView1 = heartTransactionViews1.get(0);
        assertThat(heartTransactionView1.id()).isEqualTo(heartTransaction1.getId());

        assertThat(result2).isEmpty();
    }

    private Member createMember(String phoneNumber, String nickname) {
        Member member = Member.fromPhoneNumber(phoneNumber);
        MemberProfile memberProfile = MemberProfile.builder()
            .nickname(Nickname.from(nickname))
            .build();
        member.updateProfile(memberProfile);
        return member;
    }

    private HeartTransaction createHeartTransaction(Long memberId) {
        return HeartTransaction.of(
            memberId,
            TransactionType.PURCHASE,
            TransactionType.PURCHASE.getDescription(),
            HeartAmount.from(100L),
            HeartBalance.of(100L, 200L)
        );
    }
}