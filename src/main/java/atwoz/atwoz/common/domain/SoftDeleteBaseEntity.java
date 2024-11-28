package atwoz.atwoz.common.domain;


import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@MappedSuperclass
@SQLRestriction("deleted_at IS NULL")
public abstract class SoftDeleteBaseEntity extends BaseEntity {
    private LocalDateTime deletedAt;

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}