package atwoz.atwoz.admin.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ScreeningDetailQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ScreeningDetailView findById(Long screeningId) {
        return null;
    }
}
