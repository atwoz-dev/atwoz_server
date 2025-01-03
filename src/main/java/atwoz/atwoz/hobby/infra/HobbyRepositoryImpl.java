package atwoz.atwoz.hobby.infra;

import atwoz.atwoz.hobby.domain.HobbyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HobbyRepositoryImpl implements HobbyRepository {
    private final HobbyJpaRepository hobbyJpaRepository;

    @Override
    public long countHobbiesByIdIn(List<Long> ids) {
        return hobbyJpaRepository.countAllByIdIsIn(ids);
    }
}
