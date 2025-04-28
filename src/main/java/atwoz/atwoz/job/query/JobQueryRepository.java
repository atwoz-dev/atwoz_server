package atwoz.atwoz.job.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static atwoz.atwoz.admin.command.domain.job.QJob.job;

@Repository
@RequiredArgsConstructor
public class JobQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<JobView> findAll() {
        return queryFactory.select(new QJobView(job.id, job.name))
                .from(job)
                .fetch();
    }
}
