package atwoz.atwoz.community.command.application.selfintroduction;

import atwoz.atwoz.community.command.domain.selfintroduction.SelfIntroductionCommandRepository;
import atwoz.atwoz.community.presentation.selfintroduction.dto.SelfIntroductionWriteRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SelfIntroductionServiceTest {

    @Mock
    private SelfIntroductionCommandRepository selfIntroductionCommandRepository;

    @InjectMocks
    private SelfIntroductionService selfIntroductionService;

    @DisplayName("셀프 소개를 작성한다.")
    @Test
    void writeSelfIntroduction() {
        // Given
        Long memberId = 1L;
        String title = "셀프 소개 제목";
        String content = "셀프 소개 내용입니다. 최소 내용이 30자 이상입니다~!!! (30자 이상)";

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
