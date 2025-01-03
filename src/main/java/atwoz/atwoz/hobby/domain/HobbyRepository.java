package atwoz.atwoz.hobby.domain;

import java.util.List;

public interface HobbyRepository {
    long countHobbiesByIdIn(List<Long> ids);
}
