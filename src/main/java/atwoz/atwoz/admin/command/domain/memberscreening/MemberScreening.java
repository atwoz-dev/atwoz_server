package atwoz.atwoz.admin.command.domain.memberscreening;

import atwoz.atwoz.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
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

    @Getter
    private Long memberId;

    @Getter
    private Long adminId;

    @Enumerated(STRING)
    @Column(columnDefinition = "varchar(50)")
    @Getter
    private RejectionReasonType rejectionReason;

    @Enumerated(STRING)
    @Column(columnDefinition = "varchar(50)")
    @Getter
    private ScreeningStatus status;

    @Version
    private Long version;

    public static MemberScreening from(Long memberId) {
        return new MemberScreening(memberId, null, null, ScreeningStatus.PENDING);
    }

    private MemberScreening(Long memberId, Long adminId, RejectionReasonType rejectionReason, ScreeningStatus status) {
        this.memberId = memberId;
        this.adminId = adminId;
        this.rejectionReason = rejectionReason;
        this.status = status;
    }

    public void approve(Long adminId) {
        setAdminId(adminId);
        changeScreeningStatus(ScreeningStatus.APPROVED);
        setRejectionReason(null);
    }

    public void reject(Long adminId, RejectionReasonType rejectionReason) {
        setAdminId(adminId);
        changeScreeningStatus(ScreeningStatus.REJECTED);
        setRejectionReason(rejectionReason);
    }

    private void changeScreeningStatus(ScreeningStatus status) {
        this.status = status;
    }

    private void setAdminId(@NonNull Long adminId) {
        this.adminId = adminId;
    }

    private void setRejectionReason(RejectionReasonType rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
