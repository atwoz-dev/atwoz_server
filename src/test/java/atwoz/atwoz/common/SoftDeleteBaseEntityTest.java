package atwoz.atwoz.common;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SoftDeleteBaseEntityTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("SoftDeleteBaseEntity를 상속받은 엔티티 isDeleted 기본값은 false")
    void saveSoftDeleteBaseEntityTest() {
        // given
        SoftDeleteBaseEntityTestEntity entity = new SoftDeleteBaseEntityTestEntity();

        // when
        boolean result = entity.isDeleted();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("SoftDeleteBaseEntity를 상속받은 엔티티에 @SQLDelete로 soft delete 적용하고 삭제 시 isDeleted 값이 true로 변경")
    void deleteSoftDeleteBaseEntityWithSQLDeleteTest() {
        // given
        SoftDeleteBaseEntityTestEntity entity = new SoftDeleteBaseEntityTestEntity();
        entityManager.persist(entity);

        // when
        entityManager.remove(entity);
        entityManager.flush();
        SoftDeleteBaseEntityTestEntity deletedEntity = entityManager.find(SoftDeleteBaseEntityTestEntity.class, entity.getId());

        // then
        assertThat(deletedEntity.isDeleted()).isTrue();
    }
}