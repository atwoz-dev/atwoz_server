package atwoz.atwoz.community.query.selfintroduction.view;

import atwoz.atwoz.member.command.domain.member.Gender;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record AdminSelfIntroductionView(
    long selfIntroductionId,
    String nickname,
    @Schema(implementation = Gender.class)
    String gender,
    boolean isOpened,
    String content,
    String createdDate,
    String updatedDate,
    String deletedDate
) {
    @QueryProjection
    public AdminSelfIntroductionView(
        long selfIntroductionId,
        String nickname,
        String gender,
        boolean isOpened,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
    ) {
        this(
            selfIntroductionId,
            nickname,
            gender,
            isOpened,
            content,
            formatDate(createdAt),
            formatDate(updatedAt),
            formatDate(deletedAt)
        );
    }


    private static String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? DateTimeFormatter.ofPattern("yyyy/MM/dd").format(dateTime) : null;
    }
}
