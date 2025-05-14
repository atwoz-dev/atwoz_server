package atwoz.atwoz.admin.command.domain.warning;

import atwoz.atwoz.common.event.Events;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(
    name = "warnings",
    indexes = @Index(name = "idx_member_id", columnList = "memberId")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Warning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId;

    private Long memberId;

    @Enumerated(STRING)
    @Column(columnDefinition = "varchar(50)")
    private WarningReasonType reasonType;

    private Warning(long adminId, long memberId, @NonNull WarningReasonType reasonType) {
        this.adminId = adminId;
        this.memberId = memberId;
        this.reasonType = reasonType;
    }

    public static Warning of(long adminId, long memberId, WarningReasonType reasonType) {
        Events.raise(WarningIssuedEvent.of(adminId, memberId, reasonType.toString()));
        return new Warning(adminId, memberId, reasonType);
    }
}
