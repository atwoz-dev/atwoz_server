package deepple.deepple.admin.command.application.screening;

import deepple.deepple.admin.command.domain.screening.RejectionReasonType;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ScreeningMapper {

    public static RejectionReasonType toRejectionReasonType(String rejectionReason) {
        try {
            return RejectionReasonType.valueOf(rejectionReason.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRejectionReasonException(rejectionReason.toUpperCase());
        }
    }
}
