package atwoz.atwoz.heart.query.hearttransaction.condition;

import java.time.LocalDate;

public record HeartTransactionSearchCondition(
    String nickname,
    String phoneNumber,
    LocalDate createdDateGoe,
    LocalDate createdDateLoe
) {
}
