package atwoz.atwoz.payment.query.heartpurchaseoption;

import atwoz.atwoz.common.config.QueryDslConfig;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.HeartPurchaseAmount;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.HeartPurchaseOption;
import atwoz.atwoz.payment.command.domain.heartpurchaseoption.Price;
import atwoz.atwoz.payment.query.heartpurchaseoption.condition.HeartPurchaseOptionSearchCondition;
import atwoz.atwoz.payment.query.heartpurchaseoption.view.HeartPurchaseOptionView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDslConfig.class, HeartPurchaseOptionQueryRepository.class})
class HeartPurchaseOptionQueryRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private HeartPurchaseOptionQueryRepository heartPurchaseOptionQueryRepository;

    @Test
    @DisplayName("하트 구매 옵션 페이지 조회 파라미터 순서 테스트")
    void findPage() {
        // given
        HeartPurchaseOptionSearchCondition condition = new HeartPurchaseOptionSearchCondition(
            null,
            null,
            null,
            null
        );
        PageRequest pageRequest = PageRequest.of(0, 10);

        final HeartPurchaseOption productId1 = HeartPurchaseOption.of(
            HeartPurchaseAmount.from(100L),
            Price.from(1000L),
            "productId1",
            "하트 구매 옵션 1"
        );
        entityManager.persist(productId1);
        entityManager.flush();

        // when
        final Page<HeartPurchaseOptionView> result = heartPurchaseOptionQueryRepository.findPage(condition,
            pageRequest);

        // then
        assertThat(result).isNotNull();
        List<HeartPurchaseOptionView> heartPurchaseOptionViews = result.getContent();
        assertThat(heartPurchaseOptionViews).hasSize(1);
        HeartPurchaseOptionView heartPurchaseOptionView = heartPurchaseOptionViews.get(0);
        assertThat(heartPurchaseOptionView.id()).isEqualTo(productId1.getId());
        assertThat(heartPurchaseOptionView.name()).isEqualTo(productId1.getName());
        assertThat(heartPurchaseOptionView.productId()).isEqualTo(productId1.getProductId());
        assertThat(heartPurchaseOptionView.heartAmount()).isEqualTo(productId1.getHeartAmount());
        assertThat(heartPurchaseOptionView.price()).isEqualTo(productId1.getPrice().getValue());
        assertThat(heartPurchaseOptionView.createdAt()).isEqualTo(productId1.getCreatedAt());
        assertThat(heartPurchaseOptionView.deletedAt()).isEqualTo(productId1.getDeletedAt());
    }

    @Test
    @DisplayName("하트 구매 옵션 페이지 조회 name condition 테스트")
    void findPageWithNameCondition() {
        // given
        HeartPurchaseOptionSearchCondition condition = new HeartPurchaseOptionSearchCondition(
            "Id1",
            null,
            null,
            null
        );
        PageRequest pageRequest = PageRequest.of(0, 10);

        final HeartPurchaseOption productId1 = HeartPurchaseOption.of(
            HeartPurchaseAmount.from(100L),
            Price.from(1000L),

            "productId1",
            "하트 구매 옵션 1"
        );
        final HeartPurchaseOption productId2 = HeartPurchaseOption.of(
            HeartPurchaseAmount.from(100L),
            Price.from(1000L),
            "productId2",
            "하트 구매 옵션 2"
        );
        entityManager.persist(productId1);
        entityManager.persist(productId2);
        entityManager.flush();

        // when
        final Page<HeartPurchaseOptionView> result = heartPurchaseOptionQueryRepository.findPage(condition,
            pageRequest);

        // then
        assertThat(result).isNotNull();
        List<HeartPurchaseOptionView> heartPurchaseOptionViews = result.getContent();
        assertThat(heartPurchaseOptionViews).hasSize(1);
        HeartPurchaseOptionView heartPurchaseOptionView = heartPurchaseOptionViews.get(0);
        assertThat(heartPurchaseOptionView.id()).isEqualTo(productId1.getId());
    }

    @Test
    @DisplayName("하트 구매 옵션 페이지 조회 productId condition 테스트")
    void findPageWithProductIdCondition() {
        // given
        HeartPurchaseOptionSearchCondition condition = new HeartPurchaseOptionSearchCondition(
            null,
            "옵션 1",
            null,
            null
        );
        PageRequest pageRequest = PageRequest.of(0, 10);

        final HeartPurchaseOption productId1 = HeartPurchaseOption.of(
            HeartPurchaseAmount.from(100L),
            Price.from(1000L),

            "productId1",
            "하트 구매 옵션 1"
        );
        final HeartPurchaseOption productId2 = HeartPurchaseOption.of(
            HeartPurchaseAmount.from(100L),
            Price.from(1000L),
            "productId2",
            "하트 구매 옵션 2"
        );
        entityManager.persist(productId1);
        entityManager.persist(productId2);
        entityManager.flush();

        // when
        final Page<HeartPurchaseOptionView> result = heartPurchaseOptionQueryRepository.findPage(condition,
            pageRequest);

        // then
        assertThat(result).isNotNull();
        List<HeartPurchaseOptionView> heartPurchaseOptionViews = result.getContent();
        assertThat(heartPurchaseOptionViews).hasSize(1);
        HeartPurchaseOptionView heartPurchaseOptionView = heartPurchaseOptionViews.get(0);
        assertThat(heartPurchaseOptionView.id()).isEqualTo(productId1.getId());
    }

    @Test
    @DisplayName("하트 구매 옵션 페이지 조회 createdDateGoe condition 테스트")
    void findPageWithCreatedDateGoeCondition() {
        // given
        final HeartPurchaseOption productId1 = HeartPurchaseOption.of(
            HeartPurchaseAmount.from(100L),
            Price.from(1000L),
            "productId1",
            "하트 구매 옵션 1"
        );
        entityManager.persist(productId1);
        entityManager.flush();

        HeartPurchaseOptionSearchCondition condition1 = new HeartPurchaseOptionSearchCondition(
            null,
            null,
            productId1.getCreatedAt().toLocalDate(),
            null
        );

        HeartPurchaseOptionSearchCondition condition2 = new HeartPurchaseOptionSearchCondition(
            null,
            null,
            productId1.getCreatedAt().toLocalDate().plusDays(1),
            null
        );

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        final Page<HeartPurchaseOptionView> result1 = heartPurchaseOptionQueryRepository.findPage(condition1,
            pageRequest);
        final Page<HeartPurchaseOptionView> result2 = heartPurchaseOptionQueryRepository.findPage(condition2,
            pageRequest);

        // then
        assertThat(result1).isNotNull();
        List<HeartPurchaseOptionView> heartPurchaseOptionViews1 = result1.getContent();
        assertThat(heartPurchaseOptionViews1).hasSize(1);
        HeartPurchaseOptionView heartPurchaseOptionView1 = heartPurchaseOptionViews1.get(0);
        assertThat(heartPurchaseOptionView1.id()).isEqualTo(productId1.getId());

        assertThat(result2).isNotNull();
        List<HeartPurchaseOptionView> heartPurchaseOptionViews2 = result2.getContent();
        assertThat(heartPurchaseOptionViews2).isEmpty();
    }

    @Test
    @DisplayName("하트 구매 옵션 페이지 조회 createdDateLoe condition 테스트")
    void findPageWithCreatedDateLoeCondition() {
        // given
        final HeartPurchaseOption productId1 = HeartPurchaseOption.of(
            HeartPurchaseAmount.from(100L),
            Price.from(1000L),
            "productId1",
            "하트 구매 옵션 1"
        );
        entityManager.persist(productId1);
        entityManager.flush();

        HeartPurchaseOptionSearchCondition condition1 = new HeartPurchaseOptionSearchCondition(
            null,
            null,
            null,
            productId1.getCreatedAt().toLocalDate()
        );

        HeartPurchaseOptionSearchCondition condition2 = new HeartPurchaseOptionSearchCondition(
            null,
            null,
            null,
            productId1.getCreatedAt().toLocalDate().minusDays(1)
        );

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        final Page<HeartPurchaseOptionView> result1 = heartPurchaseOptionQueryRepository.findPage(condition1,
            pageRequest);
        final Page<HeartPurchaseOptionView> result2 = heartPurchaseOptionQueryRepository.findPage(condition2,
            pageRequest);

        // then
        assertThat(result1).isNotNull();
        List<HeartPurchaseOptionView> heartPurchaseOptionViews1 = result1.getContent();
        assertThat(heartPurchaseOptionViews1).hasSize(1);
        HeartPurchaseOptionView heartPurchaseOptionView1 = heartPurchaseOptionViews1.get(0);
        assertThat(heartPurchaseOptionView1.id()).isEqualTo(productId1.getId());

        assertThat(result2).isNotNull();
        List<HeartPurchaseOptionView> heartPurchaseOptionViews2 = result2.getContent();
        assertThat(heartPurchaseOptionViews2).isEmpty();
    }
}