package atwoz.atwoz.hobby.command.infra;

import atwoz.atwoz.hobby.command.domain.HobbyCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class HobbyCommandRepositoryImpl implements HobbyCommandRepository {
    private final HobbyJpaRepository hobbyJpaRepository;

    @Override
    public long countHobbiesByIdIn(Set<Long> ids) {
        return hobbyJpaRepository.countAllByIdIsIn(ids);
    }
}
