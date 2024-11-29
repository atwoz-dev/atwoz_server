package atwoz.atwoz.common.domain;


import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class SoftDeleteBaseEntity extends BaseEntity {
    private LocalDateTime deletedAt;

    public void softDelete() {
        validateSoftDelete();
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    private void validateSoftDelete() {
        if (isDeleted()) {
            throw new IllegalStateException("이미 삭제된 엔티티 입니다.");
        }
    }
}