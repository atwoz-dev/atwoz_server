package atwoz.atwoz.report.query;

import atwoz.atwoz.member.command.domain.member.QMember;
import atwoz.atwoz.report.query.condition.ReportSearchCondition;
import atwoz.atwoz.report.query.view.QReportView;
import atwoz.atwoz.report.query.view.ReportView;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static atwoz.atwoz.report.command.domain.QReport.report;

@Repository
@RequiredArgsConstructor
public class ReportQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Page<ReportView> getPage(ReportSearchCondition condition, Pageable pageable) {
        QMember reporterMember = new QMember("reporterMember");
        QMember reporteeMember = new QMember("reporteeMember");

        List<ReportView> views = queryFactory
            .select(new QReportView(
                report.id,
                report.reporterId,
                reporterMember.profile.nickname.value,
                report.reporteeId,
                reporteeMember.profile.nickname.value,
                report.reason.stringValue(),
                report.result.stringValue(),
                report.createdAt
            ))
            .from(report)
            .where(
                resultEq(condition.result()),
                createdAtGoe(condition.createdAtGoe()),
                createdAtLoe(condition.createdAtGoe())
            )
            .leftJoin(reporterMember).on(report.reporterId.eq(reporterMember.id))
            .leftJoin(reporteeMember).on(report.reporteeId.eq(reporteeMember.id))
            .orderBy(report.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long totalCount = queryFactory
            .select(report.count())
            .from(report)
            .where(
                resultEq(condition.result()),
                createdAtGoe(condition.createdAtGoe()),
                createdAtLoe(condition.createdAtGoe())
            )
            .leftJoin(reporterMember).on(report.reporterId.eq(reporterMember.id))
            .leftJoin(reporteeMember).on(report.reporteeId.eq(reporteeMember.id))
            .fetchOne();

        return new PageImpl<>(views, pageable, totalCount);
    }

    private BooleanExpression resultEq(String result) {
        return result != null ? report.result.stringValue().eq(result) : null;
    }

    private BooleanExpression createdAtGoe(LocalDate createdDateGoe) {
        return createdDateGoe != null ? report.createdAt.goe(createdDateGoe.atStartOfDay()) : null;
    }

    private BooleanExpression createdAtLoe(LocalDate createdDateLoe) {
        return createdDateLoe != null ? report.createdAt.loe(
            createdDateLoe.plusDays(1).atStartOfDay().minusSeconds(1)) : null;
    }
}
