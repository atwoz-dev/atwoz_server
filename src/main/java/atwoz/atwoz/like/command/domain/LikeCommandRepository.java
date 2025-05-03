package atwoz.atwoz.like.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeCommandRepository extends JpaRepository<Like, Long> {
}
