package atwoz.atwoz.interview.command.domain.answer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewAnswerCommandRepository extends JpaRepository<InterviewAnswer, Long> {
    boolean existsByQuestionIdAndMemberId(Long questionId, Long memberId);
    boolean existsByMemberId(Long memberId);
}
