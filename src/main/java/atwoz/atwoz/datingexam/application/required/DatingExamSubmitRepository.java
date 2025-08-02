package atwoz.atwoz.datingexam.application.required;

import atwoz.atwoz.datingexam.domain.DatingExamSubmit;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface DatingExamSubmitRepository extends Repository<DatingExamSubmit, Long> {
    DatingExamSubmit save(DatingExamSubmit datingExamSubmit);

    boolean existsByMemberId(Long memberId);

    Optional<DatingExamSubmit> findByMemberId(Long memberId);
}
