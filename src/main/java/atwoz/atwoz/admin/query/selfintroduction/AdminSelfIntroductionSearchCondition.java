package atwoz.atwoz.admin.query.selfintroduction;

import java.time.LocalDate;

public record AdminSelfIntroductionSearchCondition(
    Boolean isOpened,
    String nickname,
    String phoneNumber,
    LocalDate startDate,
    LocalDate endDate
) {
}
