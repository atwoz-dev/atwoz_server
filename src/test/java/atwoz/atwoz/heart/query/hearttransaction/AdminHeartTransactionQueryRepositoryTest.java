package atwoz.atwoz.heart.query.hearttransaction;

import atwoz.atwoz.common.config.QueryDslConfig;
import atwoz.atwoz.heart.command.domain.hearttransaction.HeartTransaction;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartAmount;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.HeartBalance;
import atwoz.atwoz.heart.command.domain.hearttransaction.vo.TransactionType;
import atwoz.atwoz.heart.query.hearttransaction.condition.AdminHeartTransactionSearchCondition;
import atwoz.atwoz.heart.query.hearttransaction.view.AdminHeartTransactionView;
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
@Import({QueryDslConfig.class, AdminHeartTransactionQueryRepository.class})
class AdminHeartTransactionQueryRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private AdminHeartTransactionQueryRepository adminHeartTransactionQueryRepository;

    @Test
    @DisplayName("하트 거래 내역 페이지 조회 파라미터 순서 테스트")
    void findPage() {
        // given
        AdminHeartTransactionSearchCondition condition = new AdminHeartTransactionSearchCondition(
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
        final Page<AdminHeartTransactionView> result = adminHeartTransactionQueryRepository.findPage(condition,
            pageRequest);

        // then
        assertThat(result).isNotNull();
        List<AdminHeartTransactionView> adminHeartTransactionViews = result.getContent();
        assertThat(adminHeartTransactionViews).hasSize(1);
        AdminHeartTransactionView adminHeartTransactionView = adminHeartTransactionViews.get(0);
        assertThat(adminHeartTransactionView.id()).isEqualTo(heartTransaction1.getId());
        assertThat(adminHeartTransactionView.transactionType()).isEqualTo(
            heartTransaction1.getTransactionType().name());
        assertThat(adminHeartTransactionView.content()).isEqualTo(heartTransaction1.getContent());
        assertThat(adminHeartTransactionView.heartAmount()).isEqualTo(heartTransaction1.getHeartAmount().getAmount());
        Long heartBalance = heartTransaction1.getHeartBalance().getMissionHeartBalance() +
            heartTransaction1.getHeartBalance().getPurchaseHeartBalance();
        assertThat(adminHeartTransactionView.heartBalance()).isEqualTo(heartBalance);
        assertThat(adminHeartTransactionView.createdAt()).isCloseTo(heartTransaction1.getCreatedAt(),
            within(1, ChronoUnit.MICROS));
    }

    @Test
    @DisplayName("하트 거래 내역 페이지 조회 nickname condition 테스트")
    void findPageWithNicknameCondition() {
        // given
        AdminHeartTransactionSearchCondition condition = new AdminHeartTransactionSearchCondition(
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
        final Page<AdminHeartTransactionView> result = adminHeartTransactionQueryRepository.findPage(condition,
            pageRequest);

        // then
        assertThat(result).isNotNull();
        List<AdminHeartTransactionView> adminHeartTransactionViews = result.getContent();
        assertThat(adminHeartTransactionViews).hasSize(1);
        AdminHeartTransactionView adminHeartTransactionView = adminHeartTransactionViews.get(0);
        assertThat(adminHeartTransactionView.id()).isEqualTo(heartTransaction1.getId());
    }

    @Test
    @DisplayName("하트 거래 내역 페이지 조회 phoneNumber condition 테스트")
    void findPageWithPhoneNumberCondition() {
        // given
        AdminHeartTransactionSearchCondition condition = new AdminHeartTransactionSearchCondition(
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
        final Page<AdminHeartTransactionView> result = adminHeartTransactionQueryRepository.findPage(condition,
            pageRequest);

        // then
        assertThat(result).isNotNull();
        List<AdminHeartTransactionView> adminHeartTransactionViews = result.getContent();
        assertThat(adminHeartTransactionViews).hasSize(1);
        AdminHeartTransactionView adminHeartTransactionView = adminHeartTransactionViews.get(0);
        assertThat(adminHeartTransactionView.id()).isEqualTo(heartTransaction1.getId());
    }

    @Test
    @DisplayName("하트 거래 내역 페이지 조회 nickname and phoneNumber condition 테스트")
    void findPageWithNicknameAndPhoneNumberCondition() {
        // given
        AdminHeartTransactionSearchCondition condition1 = new AdminHeartTransactionSearchCondition(
            "name1",
            "01000000000",
            null,
            null
        );
        AdminHeartTransactionSearchCondition condition2 = new AdminHeartTransactionSearchCondition(
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
        final Page<AdminHeartTransactionView> result1 = adminHeartTransactionQueryRepository.findPage(condition1,
            pageRequest);
        final Page<AdminHeartTransactionView> result2 = adminHeartTransactionQueryRepository.findPage(condition2,
            pageRequest);

        // then
        assertThat(result1).isNotNull();
        List<AdminHeartTransactionView> adminHeartTransactionViews = result1.getContent();
        assertThat(adminHeartTransactionViews).hasSize(1);
        AdminHeartTransactionView adminHeartTransactionView = adminHeartTransactionViews.get(0);
        assertThat(adminHeartTransactionView.id()).isEqualTo(heartTransaction1.getId());

        assertThat(result2).isEmpty();
    }

    @Test
    @DisplayName("하트 거래 내역 페이지 조회 createdDateGoe condition 테스트")
    void findPageWithCreatedDateGoeCondition() {
        // given
        HeartTransaction heartTransaction1 = createHeartTransaction(1L);
        entityManager.persist(heartTransaction1);
        entityManager.flush();

        AdminHeartTransactionSearchCondition condition1 = new AdminHeartTransactionSearchCondition(
            null,
            null,
            heartTransaction1.getCreatedAt().toLocalDate(),
            null
        );

        AdminHeartTransactionSearchCondition condition2 = new AdminHeartTransactionSearchCondition(
            null,
            null,
            heartTransaction1.getCreatedAt().toLocalDate().plusDays(1),
            null
        );
        PageRequest pageRequest = PageRequest.of(0, 10);


        // when
        final Page<AdminHeartTransactionView> result1 = adminHeartTransactionQueryRepository.findPage(condition1,
            pageRequest);

        final Page<AdminHeartTransactionView> result2 = adminHeartTransactionQueryRepository.findPage(condition2,
            pageRequest);

        // then
        assertThat(result1).isNotNull();
        List<AdminHeartTransactionView> adminHeartTransactionViews1 = result1.getContent();
        assertThat(adminHeartTransactionViews1).hasSize(1);
        AdminHeartTransactionView adminHeartTransactionView1 = adminHeartTransactionViews1.get(0);
        assertThat(adminHeartTransactionView1.id()).isEqualTo(heartTransaction1.getId());

        assertThat(result2).isEmpty();
    }

    @Test
    @DisplayName("하트 거래 내역 페이지 조회 createdDateLoe condition 테스트")
    void findPageWithCreatedDateLoeCondition() {
        // given
        HeartTransaction heartTransaction1 = createHeartTransaction(1L);
        entityManager.persist(heartTransaction1);
        entityManager.flush();

        AdminHeartTransactionSearchCondition condition1 = new AdminHeartTransactionSearchCondition(
            null,
            null,
            null,
            heartTransaction1.getCreatedAt().toLocalDate()
        );

        AdminHeartTransactionSearchCondition condition2 = new AdminHeartTransactionSearchCondition(
            null,
            null,
            null,
            heartTransaction1.getCreatedAt().toLocalDate().minusDays(1)
        );
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        final Page<AdminHeartTransactionView> result1 = adminHeartTransactionQueryRepository.findPage(condition1,
            pageRequest);

        final Page<AdminHeartTransactionView> result2 = adminHeartTransactionQueryRepository.findPage(condition2,
            pageRequest);

        // then
        assertThat(result1).isNotNull();
        List<AdminHeartTransactionView> adminHeartTransactionViews1 = result1.getContent();
        assertThat(adminHeartTransactionViews1).hasSize(1);
        AdminHeartTransactionView adminHeartTransactionView1 = adminHeartTransactionViews1.get(0);
        assertThat(adminHeartTransactionView1.id()).isEqualTo(heartTransaction1.getId());

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