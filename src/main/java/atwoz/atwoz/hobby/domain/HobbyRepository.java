package atwoz.atwoz.hobby.domain;

import java.util.Set;

public interface HobbyRepository {
    long countHobbiesByIdIn(Set<Long> ids);
}
