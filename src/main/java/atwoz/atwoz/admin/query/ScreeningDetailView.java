package atwoz.atwoz.admin.query;

import java.util.List;

public record ScreeningDetailView(
    long screeningId,
    long version,
    ScreeningDetailProfileView profile,
    List<ScreeningDetailProfileImageView> profileImages,
    List<ScreeningDetailInterviewView> interviews
) {
}
