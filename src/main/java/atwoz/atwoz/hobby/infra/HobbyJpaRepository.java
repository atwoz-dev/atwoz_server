package atwoz.atwoz.hobby.infra;

import atwoz.atwoz.hobby.domain.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HobbyJpaRepository extends JpaRepository<Hobby, Long> {
    long countAllByIdIsIn(List<Long> ids);
}
