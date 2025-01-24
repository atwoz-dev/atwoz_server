package atwoz.atwoz.hobby.domain;

import java.util.List;
import java.util.Set;

public interface HobbyRepository {
    long countHobbiesByIdIn(Set<Long> ids);
    List<Hobby> findHobbiesByIdIn(Set<Long> ids);
}
