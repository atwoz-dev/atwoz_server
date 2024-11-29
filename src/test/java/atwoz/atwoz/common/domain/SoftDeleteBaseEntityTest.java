package atwoz.atwoz.common.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SoftDeleteBaseEntityTest {

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
    @DisplayName("softDelete 메서드 호출시 isDeleted 값이 true로 변경")
    void softDeleteTest() {
        // given
        SoftDeleteBaseEntityTestEntity entity = new SoftDeleteBaseEntityTestEntity();

        // when
        entity.softDelete();

        // then
        assertThat(entity.isDeleted()).isTrue();
    }
}