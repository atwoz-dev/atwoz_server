package atwoz.atwoz.admin.command.application.memberscreening;

import atwoz.atwoz.admin.command.domain.memberscreening.RejectionReasonType;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MemberScreeningMapper {

    public static RejectionReasonType toRejectionReasonType(String rejectionReason) {
        try {
            return RejectionReasonType.valueOf(rejectionReason.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRejectionReasonException(rejectionReason.toUpperCase());
        }
    }
}
