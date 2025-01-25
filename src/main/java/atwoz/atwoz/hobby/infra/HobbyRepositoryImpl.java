package atwoz.atwoz.hobby.infra;

import atwoz.atwoz.hobby.domain.Hobby;
import atwoz.atwoz.hobby.domain.HobbyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class HobbyRepositoryImpl implements HobbyRepository {
    private final HobbyJpaRepository hobbyJpaRepository;

    @Override
    public long countAllByIdIsIn(Set<Long> ids) {
        return hobbyJpaRepository.countAllByIdIsIn(ids);
    }

    @Override
    public List<Hobby> findByIdIn(Set<Long> ids) {
        return hobbyJpaRepository.findByIdIn(ids);
    }
}
