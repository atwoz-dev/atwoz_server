package atwoz.atwoz.heart.query.hearttransaction.condition;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record HeartTransactionSearchCondition(
    String nickname,
    String phoneNumber,
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate createdDateGoe,
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate createdDateLoe
) {
}
