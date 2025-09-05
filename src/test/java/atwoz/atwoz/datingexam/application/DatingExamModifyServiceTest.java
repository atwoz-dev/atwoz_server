package atwoz.atwoz.datingexam.application;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.datingexam.application.dto.AllRequiredSubjectSubmittedEvent;
import atwoz.atwoz.datingexam.application.dto.DatingExamInfoResponse;
import atwoz.atwoz.datingexam.application.required.DatingExamQueryRepository;
import atwoz.atwoz.datingexam.application.required.DatingExamSubjectRepository;
import atwoz.atwoz.datingexam.application.required.DatingExamSubmitRepository;
import atwoz.atwoz.datingexam.domain.DatingExamAnswerEncoder;
import atwoz.atwoz.datingexam.domain.DatingExamSubject;
import atwoz.atwoz.datingexam.domain.DatingExamSubmit;
import atwoz.atwoz.datingexam.domain.SubjectType;
import atwoz.atwoz.datingexam.domain.dto.DatingExamSubmitRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatingExamModifyServiceTest {

    @InjectMocks
    private DatingExamModifyService datingExamModifyService;

    @Mock
    private DatingExamSubjectRepository datingExamSubjectRepository;

    @Mock
    private DatingExamSubmitRepository datingExamSubmitRepository;

    @Mock
    private DatingExamQueryRepository datingExamQueryRepository;

    @Mock
    private DatingExamAnswerEncoder answerEncoder;

    @Nested
    @DisplayName("submitSubject 메서드 테스트")
    class SubmitSubjectTests {

        @Test
        @DisplayName("과목 제출 기록이 없고 유효한 제출 요청이 주어지면, 모의고사 제출을 생성하고 저장한다.")
        void submitSubject_Success() {
            // Given
            Long memberId = 1L;
            Long subjectId = 2L;
            DatingExamSubmitRequest request = mock(DatingExamSubmitRequest.class);
            when(request.subjectId()).thenReturn(subjectId);

            DatingExamSubject subject = mock(DatingExamSubject.class);
            when(subject.getType()).thenReturn(SubjectType.REQUIRED);
            when(datingExamSubjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

            when(datingExamSubmitRepository.existsByMemberIdAndSubjectId(memberId, subjectId)).thenReturn(false);

            DatingExamInfoResponse infoResponse = mock(DatingExamInfoResponse.class);
            when(datingExamQueryRepository.findDatingExamInfo(SubjectType.REQUIRED)).thenReturn(infoResponse);

            try (MockedStatic<DatingExamSubmit> mockedDatingExamSubmit = mockStatic(DatingExamSubmit.class);
                MockedStatic<DatingExamSubmitRequestValidator> mockedValidator = mockStatic(
                    DatingExamSubmitRequestValidator.class)
            ) {
                DatingExamSubmit mockSubmit = mock(DatingExamSubmit.class);
                mockedDatingExamSubmit.when(() -> DatingExamSubmit.from(request, answerEncoder, memberId))
                    .thenReturn(mockSubmit);
                mockedValidator.when(
                        () -> DatingExamSubmitRequestValidator.validateSubmit(request, infoResponse))
                    .thenAnswer(invocation -> null);

                // When
                datingExamModifyService.submitSubject(request, memberId);

                // Then
                mockedDatingExamSubmit.verify(() -> DatingExamSubmit.from(request, answerEncoder, memberId));
                verify(datingExamSubmitRepository).save(mockSubmit);
            }
        }

        @Test
        @DisplayName("존재하지 않는 과목 ID가 주어지면 예외를 던진다.")
        void throwIfSubjectNotFound() {
            // Given
            Long memberId = 1L;
            Long subjectId = 2L;
            DatingExamSubmitRequest request = mock(DatingExamSubmitRequest.class);
            when(request.subjectId()).thenReturn(subjectId);
            when(datingExamSubjectRepository.findById(subjectId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> datingExamModifyService.submitSubject(request, memberId))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("이미 과목 제출 기록이 있는 경우 예외를 던진다.")
        void throwIfAlreadySubmitted() {
            // Given
            Long memberId = 1L;
            Long subjectId = 2L;
            DatingExamSubmitRequest request = mock(DatingExamSubmitRequest.class);
            when(request.subjectId()).thenReturn(subjectId);

            DatingExamSubject subject = mock(DatingExamSubject.class);
            when(datingExamSubjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

            when(datingExamSubmitRepository.existsByMemberIdAndSubjectId(memberId, subjectId)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> datingExamModifyService.submitSubject(request, memberId))
                .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("필수 과목 제출 후 모든 필수 과목이 제출된 경우 이벤트를 발행한다.")
        void publishEventWhenAllRequiredSubjectsSubmitted() {
            // Given
            Long memberId = 1L;
            Long subjectId = 2L;
            DatingExamSubmitRequest request = mock(DatingExamSubmitRequest.class);
            when(request.subjectId()).thenReturn(subjectId);

            DatingExamSubject subject = mock(DatingExamSubject.class);
            when(subject.getType()).thenReturn(SubjectType.REQUIRED);
            when(subject.isRequired()).thenReturn(true);
            when(subject.getId()).thenReturn(subjectId);
            when(datingExamSubjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

            when(datingExamSubmitRepository.existsByMemberIdAndSubjectId(memberId, subjectId)).thenReturn(false);

            DatingExamInfoResponse infoResponse = mock(DatingExamInfoResponse.class);
            when(datingExamQueryRepository.findDatingExamInfo(SubjectType.REQUIRED)).thenReturn(infoResponse);

            try (MockedStatic<DatingExamSubmit> mockedDatingExamSubmit = mockStatic(DatingExamSubmit.class);
                MockedStatic<DatingExamSubmitRequestValidator> mockedValidator = mockStatic(
                    DatingExamSubmitRequestValidator.class);
                MockedStatic<Events> mockedEvents = mockStatic(Events.class)) {

                DatingExamSubmit mockSubmit = mock(DatingExamSubmit.class);
                when(mockSubmit.getSubjectId()).thenReturn(subjectId);
                mockedDatingExamSubmit.when(() -> DatingExamSubmit.from(request, answerEncoder, memberId))
                    .thenReturn(mockSubmit);
                mockedValidator.when(
                        () -> DatingExamSubmitRequestValidator.validateSubmit(request, infoResponse))
                    .thenAnswer(invocation -> null);
                when(datingExamSubjectRepository.findAllByType(SubjectType.REQUIRED))
                    .thenReturn(Set.of(subject));

                when(datingExamSubmitRepository.findAllByMemberId(memberId)).thenReturn(Set.of(mockSubmit));

                // When
                datingExamModifyService.submitSubject(request, memberId);

                // Then
                mockedDatingExamSubmit.verify(() -> DatingExamSubmit.from(request, answerEncoder, memberId));
                verify(datingExamSubmitRepository).save(mockSubmit);
                mockedEvents.verify(() -> Events.raise(eq(AllRequiredSubjectSubmittedEvent.of(memberId))));
            }
        }
    }
}