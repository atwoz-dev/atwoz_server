package atwoz.atwoz.common.entity;


import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class SoftDeleteBaseEntity extends BaseEntity {
    @Getter
    private LocalDateTime deletedAt;

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}