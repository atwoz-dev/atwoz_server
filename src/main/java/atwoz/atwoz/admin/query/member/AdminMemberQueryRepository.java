package atwoz.atwoz.admin.query.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminMemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<MemberView> findMembers(MemberSearchCondition condition, Pageable pageable) {
        return null;
    }

    public MemberDetailView findById(long memberId) {
        return null;
    }
}
