package atwoz.atwoz.admin.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScreeningMemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<ScreeningMemberData> findScreeningMembers(ScreeningMemberSearchCondition condition, Pageable pageable) {

        List<ScreeningMemberData> content = null;

        long totalCount = 0;

        return new PageImpl<>(content, pageable, totalCount);
    }
}
