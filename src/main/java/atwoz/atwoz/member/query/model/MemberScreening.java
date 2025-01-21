package atwoz.atwoz.member.query.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class MemberScreening {

    private Long id;
    private String nickname;
    private String gender;
    private String screeningStatus;
    private LocalDateTime createdAt;
    private Integer warningCount;
    private String activityStatus;
    private String bannedReason;
}
