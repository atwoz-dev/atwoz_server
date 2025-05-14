package atwoz.atwoz.admin.command.application.warning;

import atwoz.atwoz.admin.command.domain.warning.WarningReasonType;
import atwoz.atwoz.admin.presentation.warning.WarningReasonRequest;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class WarningMapper {

    public static WarningReasonType toWarningReasonType(WarningReasonRequest warningReasonRequest) {
        try {
            return switch (warningReasonRequest) {
                case INAPPROPRIATE_CONTENT -> WarningReasonType.INAPPROPRIATE_CONTENT;
                case INAPPROPRIATE_PROFILE_IMAGE -> WarningReasonType.INAPPROPRIATE_PROFILE_IMAGE;
            };
        } catch (IllegalArgumentException e) {
            throw new InvalidWarningReasonTypeException(warningReasonRequest.toString());
        }
    }
}
