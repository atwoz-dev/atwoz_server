package atwoz.atwoz.admin.query;

import com.querydsl.core.annotations.QueryProjection;

public record ScreeningDetailInterviewView(
    String question,
    String answer
) {
    @QueryProjection
    public ScreeningDetailInterviewView {
    }
}
