package atwoz.atwoz.like.command.domain;

import atwoz.atwoz.like.command.domain.like.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeCommandRepository extends JpaRepository<Like, Long> {
}
