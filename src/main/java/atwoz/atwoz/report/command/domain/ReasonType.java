package atwoz.atwoz.report.command.domain;

public enum ReasonType {
    STOLEN_IMAGE("사진 도용"),
    INAPPROPRIATE_IMAGE("부적절한 사진"),
    EXPLICIT_CONTENT("과도한 성적 컨텐츠"),
    OFFENSIVE_LANGUAGE("욕설 또는 불쾌한 표현"),
    CONTACT_IN_PROFILE("프로필 연락처 노출"),
    ETC("기타");

    private final String description;

    ReasonType(String description) {
        this.description = description;
    }
}
