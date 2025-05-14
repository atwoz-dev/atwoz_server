package atwoz.atwoz.payment.query.heartpurchaseoption;

import atwoz.atwoz.payment.query.heartpurchaseoption.condition.HeartPurchaseOptionSearchCondition;
import atwoz.atwoz.payment.query.heartpurchaseoption.view.HeartPurchaseOptionView;
import atwoz.atwoz.payment.query.heartpurchaseoption.view.QHeartPurchaseOptionView;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static atwoz.atwoz.payment.command.domain.heartpurchaseoption.QHeartPurchaseOption.heartPurchaseOption;

@Repository
@RequiredArgsConstructor
public class HeartPurchaseOptionQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Page<HeartPurchaseOptionView> findPage(HeartPurchaseOptionSearchCondition condition, Pageable pageable) {
        List<HeartPurchaseOptionView> views = queryFactory
            .select(new QHeartPurchaseOptionView(
                heartPurchaseOption.id,
                heartPurchaseOption.name.stringValue(),
                heartPurchaseOption.productId.stringValue(),
                heartPurchaseOption.amount.amount,
                heartPurchaseOption.price.value,
                heartPurchaseOption.createdAt,
                heartPurchaseOption.deletedAt
            ))
            .from(heartPurchaseOption)
            .where(
                nameContain(condition.name()),
                productIdContain(condition.productId()),
                createdAtGoe(condition.createdDateGoe()),
                createdAtLoe(condition.createdDateLoe())
            )
            .orderBy(heartPurchaseOption.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long totalCount = queryFactory
            .select(heartPurchaseOption.count())
            .from(heartPurchaseOption)
            .where(
                nameContain(condition.name()),
                productIdContain(condition.productId()),
                createdAtGoe(condition.createdDateGoe()),
                createdAtLoe(condition.createdDateLoe())
            )
            .fetchOne();

        return new PageImpl<>(views, pageable, totalCount);
    }

    private BooleanExpression nameContain(String name) {
        return name != null ? heartPurchaseOption.name.stringValue().contains(name) : null;
    }

    private BooleanExpression productIdContain(String productId) {
        return productId != null ? heartPurchaseOption.productId.stringValue().contains(productId) : null;
    }

    private BooleanExpression createdAtGoe(LocalDate createdDateGoe) {
        return createdDateGoe != null ? heartPurchaseOption.createdAt.goe(createdDateGoe.atStartOfDay()) : null;
    }

    private BooleanExpression createdAtLoe(LocalDate createdDateLoe) {
        return createdDateLoe != null ? heartPurchaseOption.createdAt.loe(
            createdDateLoe.plusDays(1).atStartOfDay().minusSeconds(1)) : null;
    }


}
