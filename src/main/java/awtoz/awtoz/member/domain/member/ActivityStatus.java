package awtoz.awtoz.member.domain.member;

import jakarta.persistence.Embeddable;

public enum ActivityStatus {
    ACTIVE, PERMANENT_STOP, WAITING;

    public boolean isPermanentStop() {
        return PERMANENT_STOP.equals(this);
    }
}
