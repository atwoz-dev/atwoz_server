package atwoz.atwoz.interview.command.domain.question;

public enum InterviewCategory {
    PERSONAL("나"),
    SOCIAL("관계"),
    ROMANTIC("연애");

    private String description;

    InterviewCategory(String description) {
        this.description = description;
    }
}
