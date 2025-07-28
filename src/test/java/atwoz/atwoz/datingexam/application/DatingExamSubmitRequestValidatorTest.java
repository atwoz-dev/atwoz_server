package atwoz.atwoz.datingexam.application;

import atwoz.atwoz.datingexam.adapter.webapi.dto.DatingExamAnswerInfo;
import atwoz.atwoz.datingexam.adapter.webapi.dto.DatingExamInfoResponse;
import atwoz.atwoz.datingexam.adapter.webapi.dto.DatingExamQuestionInfo;
import atwoz.atwoz.datingexam.adapter.webapi.dto.DatingExamSubjectInfo;
import atwoz.atwoz.datingexam.application.exception.InvalidDatingExamSubmitRequestException;
import atwoz.atwoz.datingexam.domain.SubjectType;
import atwoz.atwoz.datingexam.domain.dto.AnswerSubmitRequest;
import atwoz.atwoz.datingexam.domain.dto.DatingExamSubmitRequest;
import atwoz.atwoz.datingexam.domain.dto.SubjectSubmitRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatingExamSubmitRequestValidatorTest {

    private static DatingExamInfoResponse buildInfo(SubjectType subjectType) {
        DatingExamAnswerInfo a111 = new DatingExamAnswerInfo(111L, "ans111");
        DatingExamAnswerInfo a112 = new DatingExamAnswerInfo(112L, "ans112");
        DatingExamAnswerInfo a113 = new DatingExamAnswerInfo(113L, "ans113");
        DatingExamQuestionInfo q11 = new DatingExamQuestionInfo(11L, "q11", List.of(a111, a112));
        DatingExamQuestionInfo q12 = new DatingExamQuestionInfo(12L, "q12", List.of(a113));
        DatingExamSubjectInfo subj1 = new DatingExamSubjectInfo(
            1L, subjectType.name(), "Subj1", List.of(q11, q12)
        );

        DatingExamAnswerInfo a211 = new DatingExamAnswerInfo(211L, "ans211");
        DatingExamQuestionInfo q21 = new DatingExamQuestionInfo(21L, "q21", List.of(a211));
        DatingExamSubjectInfo subj2 = new DatingExamSubjectInfo(
            2L, subjectType.name(), "Subj2", List.of(q21)
        );

        return new DatingExamInfoResponse(List.of(subj1, subj2));
    }

    @Test
    @DisplayName("OPTIONAL 타입은 유효한 단일 과목을 제출하면, 예외를 던지지 않는다.")
    void optionalValidSingleSubject() {
        var info = buildInfo(SubjectType.OPTIONAL);
        var request = new DatingExamSubmitRequest(List.of(
            new SubjectSubmitRequest(1L, List.of(
                new AnswerSubmitRequest(11L, 111L),
                new AnswerSubmitRequest(12L, 113L)
            ))
        ));

        assertThatCode(() ->
            DatingExamSubmitRequestValidator.validateSubmit(request, info, SubjectType.OPTIONAL)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("REQUIRED 타입은 모든 과목을 제출하면, 예외를 던지지 않는다.")
    void requiredValidAllSubjects() {
        var info = buildInfo(SubjectType.REQUIRED);
        var request = new DatingExamSubmitRequest(List.of(
            new SubjectSubmitRequest(1L, List.of(
                new AnswerSubmitRequest(11L, 111L),
                new AnswerSubmitRequest(12L, 113L)
            )),
            new SubjectSubmitRequest(2L, List.of(
                new AnswerSubmitRequest(21L, 211L)
            ))
        ));

        assertThatCode(() ->
            DatingExamSubmitRequestValidator.validateSubmit(request, info, SubjectType.REQUIRED)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("OPTIONAL 타입은 subjectId가 중복되면, 예외를 던진다.")
    void duplicateSubjectIdsShouldFailOptional() {
        var info = buildInfo(SubjectType.OPTIONAL);
        var request = new DatingExamSubmitRequest(List.of(
            new SubjectSubmitRequest(1L, List.of(
                new AnswerSubmitRequest(11L, 111L)
            )),
            new SubjectSubmitRequest(1L, List.of(
                new AnswerSubmitRequest(12L, 113L)
            ))
        ));

        assertThatThrownBy(() ->
            DatingExamSubmitRequestValidator.validateSubmit(request, info, SubjectType.OPTIONAL)
        )
            .isInstanceOf(InvalidDatingExamSubmitRequestException.class)
            .hasMessageContaining("중복");
    }

    @Test
    @DisplayName("OPTIONAL 타입은 존재하지 않는 subjectId를 제출하면, 예외를 던진다.")
    void missingSubjectIdShouldFailOptional() {
        var info = buildInfo(SubjectType.OPTIONAL);
        var request = new DatingExamSubmitRequest(List.of(
            new SubjectSubmitRequest(3L, List.of(
                new AnswerSubmitRequest(11L, 111L)
            ))
        ));

        assertThatThrownBy(() ->
            DatingExamSubmitRequestValidator.validateSubmit(request, info, SubjectType.OPTIONAL)
        )
            .isInstanceOf(InvalidDatingExamSubmitRequestException.class)
            .hasMessageContaining("존재하지 않는 subjectId");
    }

    @Test
    @DisplayName("OPTIONAL 타입은 질문ID가 중복되면, 예외를 던진다.")
    void duplicateQuestionIdsShouldFailOptional() {
        var info = buildInfo(SubjectType.OPTIONAL);
        var request = new DatingExamSubmitRequest(List.of(
            new SubjectSubmitRequest(1L, List.of(
                new AnswerSubmitRequest(11L, 111L),
                new AnswerSubmitRequest(11L, 112L)
            ))
        ));

        assertThatThrownBy(() ->
            DatingExamSubmitRequestValidator.validateSubmit(request, info, SubjectType.OPTIONAL)
        )
            .isInstanceOf(InvalidDatingExamSubmitRequestException.class)
            .hasMessageContaining("중복된 questionId");
    }

    @Test
    @DisplayName("OPTIONAL 타입은 답변 개수가 올바르지 않으면, 예외를 던진다.")
    void wrongAnswerCountShouldFailOptional() {
        var info = buildInfo(SubjectType.OPTIONAL);
        var request = new DatingExamSubmitRequest(List.of(
            new SubjectSubmitRequest(1L, List.of(
                new AnswerSubmitRequest(11L, 111L)
            ))
        ));

        assertThatThrownBy(() ->
            DatingExamSubmitRequestValidator.validateSubmit(request, info, SubjectType.OPTIONAL)
        )
            .isInstanceOf(InvalidDatingExamSubmitRequestException.class)
            .hasMessageContaining("속하는 questionId의 수가 올바르지 않습니다");
    }

    @Test
    @DisplayName("OPTIONAL 타입은 존재하지 않는 questionId를 제출하면, 예외를 던진다.")
    void missingQuestionIdShouldFailOptional() {
        var info = buildInfo(SubjectType.OPTIONAL);
        var request = new DatingExamSubmitRequest(List.of(
            new SubjectSubmitRequest(1L, List.of(
                new AnswerSubmitRequest(99L, 111L),
                new AnswerSubmitRequest(12L, 113L)
            ))
        ));

        assertThatThrownBy(() ->
            DatingExamSubmitRequestValidator.validateSubmit(request, info, SubjectType.OPTIONAL)
        )
            .isInstanceOf(InvalidDatingExamSubmitRequestException.class)
            .hasMessageContaining("속하지 않은 questionId");
    }

    @Test
    @DisplayName("OPTIONAL 타입은 존재하지 않는 answerId를 제출하면, 예외를 던진다.")
    void missingAnswerIdShouldFailOptional() {
        var info = buildInfo(SubjectType.OPTIONAL);
        var request = new DatingExamSubmitRequest(List.of(
            new SubjectSubmitRequest(1L, List.of(
                new AnswerSubmitRequest(11L, 999L),
                new AnswerSubmitRequest(12L, 113L)
            ))
        ));

        assertThatThrownBy(() ->
            DatingExamSubmitRequestValidator.validateSubmit(request, info, SubjectType.OPTIONAL)
        )
            .isInstanceOf(InvalidDatingExamSubmitRequestException.class)
            .hasMessageContaining("속하지 않은 answerId");
    }

    @Test
    @DisplayName("REQUIRED 타입은 과목 수가 일치하지 않으면, 예외를 던진다.")
    void requiredSubjectCountMismatchShouldFail() {
        var info = buildInfo(SubjectType.REQUIRED);
        var request = new DatingExamSubmitRequest(List.of(
            new SubjectSubmitRequest(1L, List.of(
                new AnswerSubmitRequest(11L, 111L),
                new AnswerSubmitRequest(12L, 113L)
            ))
        ));

        assertThatThrownBy(() ->
            DatingExamSubmitRequestValidator.validateSubmit(request, info, SubjectType.REQUIRED)
        )
            .isInstanceOf(InvalidDatingExamSubmitRequestException.class)
            .hasMessageContaining("필수 과목의 수가 올바르지 않습니다");
    }
}