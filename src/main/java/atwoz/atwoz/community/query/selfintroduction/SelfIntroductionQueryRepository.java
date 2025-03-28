package atwoz.atwoz.community.query.selfintroduction;

import atwoz.atwoz.community.query.selfintroduction.view.SelfIntroductionSummaryView;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SelfIntroductionQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<SelfIntroductionSummaryView> findSelfIntroductions() {

        return null;
    }
}
