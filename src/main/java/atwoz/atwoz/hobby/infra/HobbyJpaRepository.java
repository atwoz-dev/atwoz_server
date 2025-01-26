package atwoz.atwoz.hobby.infra;

import atwoz.atwoz.hobby.domain.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface HobbyJpaRepository extends JpaRepository<Hobby, Long> {
    long countAllByIdIsIn(Set<Long> ids);
    List<Hobby> findByIdIn(Set<Long> ids);
}
