package atwoz.atwoz.admin.command.domain.screening;

import atwoz.atwoz.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "screenings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Screening extends BaseEntity {

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

    @Version
    private Long version;

    public static Screening from(long memberId) {
        return new Screening(memberId, null, null, ScreeningStatus.PENDING);
    }

    private Screening(long memberId, Long adminId, RejectionReasonType rejectionReason, ScreeningStatus status) {
        this.memberId = memberId;
        this.adminId = adminId;
        this.rejectionReason = rejectionReason;
        this.status = status;
    }

    public boolean hasVersionConflict(long version) {
        return this.version == version;
    }

    public void approve(long adminId) {
        setAdminId(adminId);
        changeScreeningStatus(ScreeningStatus.APPROVED);
        setRejectionReason(null);
    }

    public void reject(long adminId, RejectionReasonType rejectionReason) {
        validateNotApproved();
        setAdminId(adminId);
        changeScreeningStatus(ScreeningStatus.REJECTED);
        setRejectionReason(rejectionReason);
    }

    private void setAdminId(long adminId) {
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
