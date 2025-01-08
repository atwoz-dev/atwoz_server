package atwoz.atwoz.common;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.SQLDelete;

@Entity
@SQLDelete(sql = "UPDATE soft_delete_base_entity_test_entity SET deleted_at = now() WHERE id = ?")
public class SoftDeleteBaseEntityTestEntity extends SoftDeleteBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public SoftDeleteBaseEntityTestEntity() {
    }

    public Long getId() {
        return id;
    }
}