package atwoz.atwoz.admin.command.domain.suspension;

import atwoz.atwoz.common.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "suspensions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Suspension extends SoftDeleteBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private Long adminId;

    @Enumerated(STRING)
    @Column(columnDefinition = "varchar(50)")
    private SuspensionStatus status;

    private Suspension(Long memberId, Long adminId, SuspensionStatus status) {
        this.memberId = memberId;
        this.adminId = adminId;
        this.status = status;
    }

    public static Suspension of(long memberId, long adminId, @NonNull SuspensionStatus status) {
        return new Suspension(memberId, adminId, status);
    }
}
