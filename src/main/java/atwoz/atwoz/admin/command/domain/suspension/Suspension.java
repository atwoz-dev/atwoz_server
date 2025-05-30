package atwoz.atwoz.admin.command.domain.suspension;

import atwoz.atwoz.common.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(
    name = "suspensions",
    uniqueConstraints = @UniqueConstraint(columnNames = "memberId")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Suspension extends SoftDeleteBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId;

    private Long memberId;

    @Enumerated(STRING)
    @Column(columnDefinition = "varchar(50)")
    private SuspensionStatus status;

    private Suspension(Long adminId, Long memberId, SuspensionStatus status) {
        this.adminId = adminId;
        this.memberId = memberId;
        this.status = status;
    }

    public static Suspension of(long adminId, long memberId, @NonNull SuspensionStatus status) {
        return new Suspension(adminId, memberId, status);
    }

    public void updateStatus(long adminId, @NonNull SuspensionStatus status) {
        this.adminId = adminId;
        this.status = status;
    }
}
