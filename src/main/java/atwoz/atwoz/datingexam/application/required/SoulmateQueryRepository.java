package atwoz.atwoz.datingexam.application.required;

import java.util.Set;

public interface SoulmateQueryRepository {
    Set<Long> findSoulmateIds(Long memberId, String requiredSubjectAnswers);
}
