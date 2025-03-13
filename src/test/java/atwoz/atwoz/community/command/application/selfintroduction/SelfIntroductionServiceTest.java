package atwoz.atwoz.community.command.application.selfintroduction;

import atwoz.atwoz.community.command.application.selfintroduction.exception.NotMatchedMemberIdException;
import atwoz.atwoz.community.command.application.selfintroduction.exception.SelfIntroductionNotFoundException;
import atwoz.atwoz.community.command.domain.selfintroduction.SelfIntroduction;
import atwoz.atwoz.community.command.domain.selfintroduction.SelfIntroductionCommandRepository;
import atwoz.atwoz.community.presentation.selfintroduction.dto.SelfIntroductionWriteRequest;
import atwoz.atwoz.member.command.application.member.exception.MemberNotFoundException;
import atwoz.atwoz.member.command.domain.member.Member;
import atwoz.atwoz.member.command.domain.member.MemberCommandRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SelfIntroductionServiceTest {

    @Mock
    private SelfIntroductionCommandRepository selfIntroductionCommandRepository;

    @Mock
    private MemberCommandRepository memberCommandRepository;

    @InjectMocks
    private SelfIntroductionService selfIntroductionService;

    @Nested
    @DisplayName("셀프 소개 작성")
    class Write {
        @DisplayName("존재하지 않는 멤버의 ID인 경우, 예외 발생")
        @Test
        void throwExceptionWhenMemberNotFound() {
            // Given
            Long memberId = 1L;
            String title = "셀프 소개 제목";
            String content = "셀프 소개 내용입니다. 최소 내용이 30자 이상입니다~!!! (30자 이상)";

            Mockito.when(memberCommandRepository.findById(memberId)).thenReturn(Optional.empty());

            // When & Then
            Assertions.assertThatThrownBy(() -> selfIntroductionService.write(new SelfIntroductionWriteRequest(title, content), memberId))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @DisplayName("셀프 소개를 작성한다.")
        @Test
        void writeSelfIntroduction() {
            // Given
            Long memberId = 1L;
            String title = "셀프 소개 제목";
            String content = "셀프 소개 내용입니다. 최소 내용이 30자 이상입니다~!!! (30자 이상)";

            Mockito.when(memberCommandRepository.findById(memberId)).thenReturn(Optional.of(Mockito.mock(Member.class)));

            // When
            selfIntroductionService.write(new SelfIntroductionWriteRequest(title, content), memberId);

            // Then
            verify(selfIntroductionCommandRepository).save(argThat(selfIntroduction ->
                    selfIntroduction.getMemberId().equals(memberId) &&
                            selfIntroduction.getContent().equals(content) &&
                            selfIntroduction.getTitle().equals(title)
            ));
        }
    }

    @Nested
    @DisplayName("셀프 소개 업데이트")
    class Update {

        @Test
        @DisplayName("존재하지 않은 멤버의 ID인 경우, 예외 발생")
        void throwExceptionWhenMemberNotFound() {
            // Given
            Long selfIntroductionId = 1L;
            Long memberId = 1L;
            String title = "셀프 소개 제목";
            String content = "셀프 소개 내용입니다. 최소 내용이 30자 이상입니다~!!! (30자 이상)";

            Mockito.when(memberCommandRepository.findById(memberId)).thenReturn(Optional.empty());

            // When & Then
            Assertions.assertThatThrownBy(() -> selfIntroductionService.update(new SelfIntroductionWriteRequest(title, content), memberId, selfIntroductionId))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("해당 ID의 셀프 소개가 존재하지 않을 경우, 예외 발생")
        void throwExceptionWhenSelfIntroductionNotFound() {
            // Given
            Long selfIntroductionId = 1L;
            Long memberId = 1L;
            String title = "셀프 소개 제목";
            String content = "셀프 소개 내용입니다. 최소 내용이 30자 이상입니다~!!! (30자 이상)";

            Mockito.when(memberCommandRepository.findById(memberId)).thenReturn(Optional.of(Mockito.mock(Member.class)));
            Mockito.when(selfIntroductionCommandRepository.findById(selfIntroductionId)).thenReturn(Optional.empty());

            // When & Then
            Assertions.assertThatThrownBy(() -> selfIntroductionService.update(new SelfIntroductionWriteRequest(title, content), memberId, selfIntroductionId))
                    .isInstanceOf(SelfIntroductionNotFoundException.class);
        }

        @Test
        @DisplayName("수정하고자 하는 셀프 소개가 자신이 작성한 셀프 소개가 아닐 경우, 예외 발생")
        void throwExceptionWhenMemberIdFromSelfIntroductionIsNotEqualMemberId() {
            // Given
            Long selfIntroductionId = 1L;
            Long memberId = 2L;
            String title = "셀프 소개 제목";
            String content = "셀프 소개 내용입니다. 최소 내용이 30자 이상입니다~!!! (30자 이상)";

            SelfIntroduction selfIntroduction = SelfIntroduction.write(1L, title, content);

            Mockito.when(memberCommandRepository.findById(memberId)).thenReturn(Optional.of(Mockito.mock(Member.class)));
            Mockito.when(selfIntroductionCommandRepository.findById(selfIntroductionId)).thenReturn(Optional.of(selfIntroduction));

            // When & Then
            Assertions.assertThatThrownBy(() -> selfIntroductionService.update(new SelfIntroductionWriteRequest(title, content), memberId, selfIntroductionId))
                    .isInstanceOf(NotMatchedMemberIdException.class);
        }

        @Test
        @DisplayName("셀프 소개를 업데이트한다.")
        void updateSelfIntroduction() {
            // Given
            Long selfIntroductionId = 1L;
            Long memberId = 2L;
            String title = "셀프 소개 제목";
            String content = "셀프 소개 내용입니다. 최소 내용이 30자 이상입니다~!!! (30자 이상)";
            String updatedContent = "셀프 소개 내용입니다. 최소 내용이 100자 이상입니다~!!! (100자 이상)";

            SelfIntroduction selfIntroduction = SelfIntroduction.write(memberId, title, content);

            Mockito.when(memberCommandRepository.findById(memberId)).thenReturn(Optional.of(Mockito.mock(Member.class)));
            Mockito.when(selfIntroductionCommandRepository.findById(selfIntroductionId)).thenReturn(Optional.of(selfIntroduction));

            // When
            selfIntroductionService.update(new SelfIntroductionWriteRequest(title, updatedContent), memberId, selfIntroductionId);

            // When
            Assertions.assertThat(selfIntroduction.getMemberId()).isEqualTo(memberId);
            Assertions.assertThat(selfIntroduction.getContent()).isEqualTo(updatedContent);
            Assertions.assertThat(selfIntroduction.getTitle()).isEqualTo(title);
        }
    }

    @Nested
    @DisplayName("셀프 소개 삭제")
    class Delete {

        @Test
        @DisplayName("존재하지 않은 멤버의 ID인 경우, 예외 발생")
        void throwExceptionWhenMemberNotFound() {
            // Given
            Long selfIntroductionId = 1L;
            Long memberId = 2L;

            Mockito.when(memberCommandRepository.findById(memberId)).thenReturn(Optional.empty());

            // When & Then
            Assertions.assertThatThrownBy(() -> selfIntroductionService.delete(selfIntroductionId, memberId))
                    .isInstanceOf(MemberNotFoundException.class);

        }

        @Test
        @DisplayName("해당 ID의 셀프 소개가 존재하지 않을 경우, 예외 발생")
        void throwExceptionWhenSelfIntroductionNotFound() {
            // Given
            Long selfIntroductionId = 1L;
            Long memberId = 2L;

            Mockito.when(memberCommandRepository.findById(memberId)).thenReturn(Optional.of(Mockito.mock(Member.class)));
            Mockito.when(selfIntroductionCommandRepository.findById(selfIntroductionId)).thenReturn(Optional.empty());

            // When & Then
            Assertions.assertThatThrownBy(() -> selfIntroductionService.delete(selfIntroductionId, memberId))
                    .isInstanceOf(SelfIntroductionNotFoundException.class);
        }

        @Test
        @DisplayName("삭제하고자 하는 셀프 소개가 자신이 작성한 셀프 소개가 아닐 경우, 예외 발생")
        void throwExceptionWhenMemberIdFromSelfIntroductionIsNotEqualMemberId() {
            // Given
            Long selfIntroductionId = 1L;
            Long memberId = 2L;

            SelfIntroduction selfIntroduction = SelfIntroduction.write(
                    1L,
                    "셀프 소개 제목",
                    "셀프 소개 내용입니다. 최소 내용이 30자 이상입니다~!!! (30자 이상)"
            );

            Mockito.when(memberCommandRepository.findById(memberId)).thenReturn(Optional.of(Mockito.mock(Member.class)));
            Mockito.when(selfIntroductionCommandRepository.findById(selfIntroductionId)).thenReturn(Optional.of(selfIntroduction));

            // When & Then
            Assertions.assertThatThrownBy(() -> selfIntroductionService.delete(selfIntroductionId, memberId))
                    .isInstanceOf(NotMatchedMemberIdException.class);
        }

        @Test
        @DisplayName("셀프 소개를 삭제한다.")
        void deleteSelfIntroduction() {
            // Given
            Long selfIntroductionId = 1L;
            Long memberId = 2L;

            SelfIntroduction selfIntroduction = SelfIntroduction.write(
                    memberId,
                    "셀프 소개 제목",
                    "셀프 소개 내용입니다. 최소 내용이 30자 이상입니다~!!! (30자 이상)"
            );

            Mockito.when(memberCommandRepository.findById(memberId)).thenReturn(Optional.of(Mockito.mock(Member.class)));
            Mockito.when(selfIntroductionCommandRepository.findById(selfIntroductionId)).thenReturn(Optional.of(selfIntroduction));

            // When
            selfIntroductionService.delete(selfIntroductionId, memberId);

            // Then
            verify(selfIntroductionCommandRepository).deleteById(selfIntroductionId);
        }
    }


}
