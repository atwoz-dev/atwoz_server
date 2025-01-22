package atwoz.atwoz.admin.command.domain.memberscreening;

import atwoz.atwoz.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "member_screenings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberScreening extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;
    private Long adminId;

    @Enumerated(STRING)
    @Column(columnDefinition = "varchar(50)")
    private RejectionReasonType rejectionReason;

    @Enumerated(STRING)
    @Column(columnDefinition = "varchar(50)")
    private ScreeningStatus status;

    @Builder
    private MemberScreening(Long memberId, Long adminId, RejectionReasonType rejectionReason, ScreeningStatus status) {
        setMemberId(memberId);
        setAdminId(adminId);
        setRejectionReason(rejectionReason);
        setStatus(status);
    }

    private void setMemberId(@NonNull Long memberId) {
        this.memberId = memberId;
    }

    private void setAdminId(@NonNull Long adminId) {
        this.adminId = adminId;
    }

    private void setRejectionReason(RejectionReasonType rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    private void setStatus(@NonNull ScreeningStatus status) {
        this.status = status;
    }
}
