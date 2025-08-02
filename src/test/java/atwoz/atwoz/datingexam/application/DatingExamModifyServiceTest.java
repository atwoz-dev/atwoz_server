package atwoz.atwoz.datingexam.application;

import atwoz.atwoz.datingexam.application.dto.DatingExamInfoResponse;
import atwoz.atwoz.datingexam.application.exception.InvalidDatingExamSubmitRequestException;
import atwoz.atwoz.datingexam.application.required.DatingExamQueryRepository;
import atwoz.atwoz.datingexam.application.required.DatingExamSubmitRepository;
import atwoz.atwoz.datingexam.domain.DatingExamAnswerEncoder;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatingExamModifyServiceTest {

    @InjectMocks
    private DatingExamModifyService datingExamModifyService;

    @Mock
    private DatingExamSubmitRepository datingExamSubmitRepository;

    @Mock
    private DatingExamQueryRepository datingExamQueryRepository;

    @Mock
    private DatingExamAnswerEncoder answerEncoder;

    @Nested
    @DisplayName("submitRequiredSubject 메서드 테스트")
    class SubmitRequiredSubjectTests {

        @Test
        @DisplayName("필수 과목 제출 기록이 없고 유효한 제출 요청이 주어지면, 모의고사 제출을 생성하고 필수 과목 제출을 호출하고 저장한다.")
        void submitRequiredSubject_Success() {
            // Given
            Long memberId = 1L;
            DatingExamSubmitRequest request = mock(DatingExamSubmitRequest.class);
            when(datingExamSubmitRepository.existsByMemberId(memberId)).thenReturn(false);
            DatingExamInfoResponse infoResponse = mock(DatingExamInfoResponse.class);
            when(datingExamQueryRepository.findDatingExamInfo(SubjectType.REQUIRED)).thenReturn(infoResponse);

            try (MockedStatic<DatingExamSubmit> mockedDatingExamSubmit = mockStatic(DatingExamSubmit.class);
                MockedStatic<DatingExamSubmitRequestValidator> mockedValidator = mockStatic(
                    DatingExamSubmitRequestValidator.class)
            ) {
                DatingExamSubmit mockSubmit = mock(DatingExamSubmit.class);
                mockedDatingExamSubmit.when(() -> DatingExamSubmit.from(memberId)).thenReturn(mockSubmit);
                mockedValidator.when(
                        () -> DatingExamSubmitRequestValidator.validateSubmit(request, infoResponse, SubjectType.REQUIRED))
                    .thenAnswer(invocation -> null);

                // When
                datingExamModifyService.submitRequiredSubject(request, memberId);

                // Then
                verify(mockSubmit).submitRequiredSubjectAnswers(request, answerEncoder);
                mockedDatingExamSubmit.verify(() -> DatingExamSubmit.from(memberId));
                verify(datingExamSubmitRepository).save(mockSubmit);
            }
        }

        @Test
        @DisplayName("이미 필수 과목 제출 기록이 있는 경우 예외를 던진다.")
        void submitRequiredSubject_AlreadySubmitted() {
            // Given
            Long memberId = 1L;
            DatingExamSubmitRequest request = mock(DatingExamSubmitRequest.class);
            when(datingExamSubmitRepository.existsByMemberId(memberId)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> datingExamModifyService.submitRequiredSubject(request, memberId))
                .isInstanceOf(InvalidDatingExamSubmitRequestException.class);
        }
    }

    @Nested
    @DisplayName("submitOptionalSubject 메서드 테스트")
    class SubmitOptionalSubjectTests {

        @Test
        @DisplayName("필수 과목 제출 후 유효한 선택 과목 제출 요청이 주어지면, 선택 과목 제출을 호출하고 저장한다.")
        void submitOptionalSubject_Success() {
            // Given
            Long memberId = 1L;
            DatingExamSubmitRequest request = mock(DatingExamSubmitRequest.class);
            DatingExamSubmit existingSubmit = mock(DatingExamSubmit.class);
            when(datingExamSubmitRepository.findByMemberId(memberId)).thenReturn(java.util.Optional.of(existingSubmit));
            DatingExamInfoResponse infoResponse = mock(DatingExamInfoResponse.class);
            when(datingExamQueryRepository.findDatingExamInfo(SubjectType.OPTIONAL)).thenReturn(infoResponse);

            try (MockedStatic<DatingExamSubmitRequestValidator> mockedValidator = mockStatic(
                DatingExamSubmitRequestValidator.class)) {
                mockedValidator.when(
                        () -> DatingExamSubmitRequestValidator.validateSubmit(request, infoResponse, SubjectType.OPTIONAL))
                    .thenAnswer(invocation -> null);

                // When
                datingExamModifyService.submitOptionalSubject(request, memberId);

                // Then
                verify(existingSubmit).submitPreferredSubjectAnswers(request, answerEncoder);
                verify(datingExamSubmitRepository).save(existingSubmit);
            }
        }

        @Test
        @DisplayName("필수 과목 제출 기록이 없는 경우 예외를 던진다.")
        void submitOptionalSubject_NoRequiredSubmission() {
            // Given
            Long memberId = 1L;
            DatingExamSubmitRequest request = mock(DatingExamSubmitRequest.class);
            when(datingExamSubmitRepository.findByMemberId(memberId)).thenReturn(java.util.Optional.empty());

            // When & Then
            assertThatThrownBy(() -> datingExamModifyService.submitOptionalSubject(request, memberId))
                .isInstanceOf(InvalidDatingExamSubmitRequestException.class);
        }
    }
}