package atwoz.atwoz.admin.command.domain.screening;

import atwoz.atwoz.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "screenings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Screening extends BaseEntity {

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
    @Getter
    private Long version;

    public static Screening from(Long memberId) {
        return new Screening(memberId, null, null, ScreeningStatus.PENDING);
    }

    private Screening(Long memberId, Long adminId, RejectionReasonType rejectionReason, ScreeningStatus status) {
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
        validateNotApproved();
        setAdminId(adminId);
        changeScreeningStatus(ScreeningStatus.REJECTED);
        setRejectionReason(rejectionReason);
    }

    private void setAdminId(@NonNull Long adminId) {
        this.adminId = adminId;
    }

    private void changeScreeningStatus(ScreeningStatus status) {
        this.status = status;
    }

    private void setRejectionReason(RejectionReasonType rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    private void validateNotApproved() {
        if (ScreeningStatus.APPROVED == status) {
            throw new CannotRejectApprovedScreeningException();
        }
    }
}
