package atwoz.atwoz.hobby.query;

import com.querydsl.jpa.JPQLQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static atwoz.atwoz.admin.command.domain.hobby.QHobby.hobby;

@Repository
@RequiredArgsConstructor
public class HobbyQueryRepository {
    private final JPQLQueryFactory queryFactory;

    public List<HobbyView> findAll() {
        return queryFactory
                .select(new QHobbyView(hobby.id, hobby.name))
                .from(hobby)
                .fetch();
    }

}
