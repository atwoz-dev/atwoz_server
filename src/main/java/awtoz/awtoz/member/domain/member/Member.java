package awtoz.awtoz.member.domain.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private ActivityStatus activityStatus;

    public static Member createWithPhoneNumber(String phoneNumber) {
        return Member.builder()
                .phoneNumber(phoneNumber)
                .activityStatus(ActivityStatus.WAITING) // 심사대기 상태로 생성.
                .build();
    }

}
