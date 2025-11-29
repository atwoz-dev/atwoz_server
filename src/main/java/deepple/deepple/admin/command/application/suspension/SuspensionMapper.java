package deepple.deepple.admin.command.application.suspension;

import deepple.deepple.admin.command.domain.suspension.SuspensionStatus;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SuspensionMapper {

    public static SuspensionStatus toSuspensionStatus(String status) {
        try {
            return SuspensionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidSuspensionStatusException(status.toUpperCase());
        }
    }
}
