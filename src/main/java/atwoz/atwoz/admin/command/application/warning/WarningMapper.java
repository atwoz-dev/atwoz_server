package atwoz.atwoz.admin.command.application.warning;

import atwoz.atwoz.admin.command.domain.warning.WarningReasonType;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class WarningMapper {

    public static WarningReasonType toWarningReasonType(String reasonType) {
        try {
            return WarningReasonType.valueOf(reasonType.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidWarningReasonTypeException(reasonType);
        }
    }
}
