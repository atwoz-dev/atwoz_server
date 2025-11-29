package deepple.deepple.common.entity;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BaseEntityTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("BaseEntity를 상속받은 엔티티 저장 테스트")
    void saveBaseEntityTest() {
        // given
        String name = "test";
        BaseEntityTestEntity entity = new BaseEntityTestEntity(name);

        // when
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        // then
        BaseEntityTestEntity savedEntity = entityManager.find(BaseEntityTestEntity.class, entity.getId());
        assertThat(savedEntity.getId()).isEqualTo(entity.getId());
        assertThat(savedEntity.getCreatedAt()).isEqualTo(savedEntity.getUpdatedAt());
    }

    @Test
    @DisplayName("BaseEntity를 상속받은 엔티티 수정 테스트")
    void updateBaseEntityTest() {
        // given
        String name = "test";
        BaseEntityTestEntity entity = new BaseEntityTestEntity(name);
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        BaseEntityTestEntity savedEntity = entityManager.find(BaseEntityTestEntity.class, entity.getId());
        LocalDateTime createdAt = savedEntity.getCreatedAt();
        LocalDateTime updatedAt = savedEntity.getUpdatedAt();

        // when
        savedEntity.setName("updated");
        entityManager.flush();
        entityManager.clear();

        // then
        BaseEntityTestEntity updatedEntity = entityManager.find(BaseEntityTestEntity.class, entity.getId());
        assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedEntity.getCreatedAt()).isEqualTo(createdAt);
        assertThat(updatedEntity.getUpdatedAt()).isAfter(updatedAt);
    }
}
