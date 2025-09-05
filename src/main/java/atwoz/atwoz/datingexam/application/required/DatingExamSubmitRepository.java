package atwoz.atwoz.datingexam.application.required;

import atwoz.atwoz.datingexam.domain.DatingExamSubmit;
import org.springframework.data.repository.Repository;

import java.util.Set;

public interface DatingExamSubmitRepository extends Repository<DatingExamSubmit, Long> {
    DatingExamSubmit save(DatingExamSubmit datingExamSubmit);

    boolean existsByMemberIdAndSubjectId(Long memberId, Long subjectId);

    Set<DatingExamSubmit> findAllByMemberId(Long memberId);
}
