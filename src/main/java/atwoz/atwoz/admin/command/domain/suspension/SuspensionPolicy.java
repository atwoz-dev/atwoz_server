package atwoz.atwoz.admin.command.domain.suspension;

import org.springframework.stereotype.Service;

@Service
public class SuspensionPolicy {

    private static final int PERMANENT_SUSPENSION_THRESHOLD = 3;

    public SuspensionStatus evaluate(long currentWarningCount) {
        if (currentWarningCount >= PERMANENT_SUSPENSION_THRESHOLD) {
            return SuspensionStatus.PERMANENT;
        }
        return SuspensionStatus.TEMPORARY;
    }
}
