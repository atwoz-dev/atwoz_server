package atwoz.atwoz.hobby.command.domain;

import java.util.List;
import java.util.Set;

public interface HobbyRepository {
    long countAllByIdIsIn(Set<Long> ids);

    List<Hobby> findByIdIn(Set<Long> ids);
}
