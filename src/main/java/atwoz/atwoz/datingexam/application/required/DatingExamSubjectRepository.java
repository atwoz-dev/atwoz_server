package atwoz.atwoz.datingexam.application.required;

import atwoz.atwoz.datingexam.domain.DatingExamSubject;
import atwoz.atwoz.datingexam.domain.SubjectType;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.Set;

public interface DatingExamSubjectRepository extends Repository<DatingExamSubject, Long> {
    Set<DatingExamSubject> findAllByType(SubjectType type);

    Optional<DatingExamSubject> findById(Long id);
}
