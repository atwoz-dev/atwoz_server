package atwoz.atwoz.report.query.condition;

import java.time.LocalDate;

public record ReportSearchCondition(
    String result,
    LocalDate createdAtGoe,
    LocalDate createdAtLoe
) {
}
