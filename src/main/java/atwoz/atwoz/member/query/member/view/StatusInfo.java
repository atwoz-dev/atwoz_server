package atwoz.atwoz.member.query.member.view;

// TODO : 푸시알림 설정 여부, 연락처 설정 여부, 인터뷰 작성 여부, 모의고사 응시 여부, 지인차단 여부.
public record StatusInfo(
    String activityStatus,
    Boolean isVip
) {
}
