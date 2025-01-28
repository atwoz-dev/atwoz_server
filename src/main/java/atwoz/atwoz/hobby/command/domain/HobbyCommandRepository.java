package atwoz.atwoz.hobby.command.domain;

import java.util.Set;

public interface HobbyCommandRepository {
    long countHobbiesByIdIn(Set<Long> ids);
}
