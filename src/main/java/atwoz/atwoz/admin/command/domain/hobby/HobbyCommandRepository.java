package atwoz.atwoz.admin.command.domain.hobby;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface HobbyCommandRepository extends JpaRepository<Hobby, Long> {

    long countAllByIdIsIn(Set<Long> ids);
}
