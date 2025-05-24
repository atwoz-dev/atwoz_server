package atwoz.atwoz.admin.command.application.selfintroduction;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.community.command.domain.selfintroduction.event.SelfIntroductionOpenStatusChangeEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class AdminSelfIntroductionCommandServiceTest {

    AdminSelfIntroductionCommandService adminSelfIntroductionCommandService = new AdminSelfIntroductionCommandService();

    @DisplayName("셀프 소개를 비공개로 변경하기 위한 이벤트를 발행합니다.")
    @Test
    void verifyEventIsRaisedOnConvertToClose() {
        // Given
        Long selfIntroductionId = 1L;

        try (MockedStatic<Events> mockedStaticEvents = Mockito.mockStatic(Events.class)) {
            // When
            adminSelfIntroductionCommandService.convertToClose(selfIntroductionId);

            // Then - 이벤트가 호출되었는지 검증
            mockedStaticEvents.verify(() ->
                    Events.raise(Mockito.any(SelfIntroductionOpenStatusChangeEvent.class)),
                Mockito.times(1)  // 정확히 1번 호출 여부 확인
            );
        }
    }

    @DisplayName("셀프 소개를 공개로 변경하기 위한 이벤트를 발행합니다.")
    @Test
    void verifyEventIsRaisedOnConvertToOpen() {
        // Given
        Long selfIntroductionId = 1L;

        try (MockedStatic<Events> mockedStaticEvents = Mockito.mockStatic(Events.class)) {
            // When
            adminSelfIntroductionCommandService.convertToOpen(selfIntroductionId);

            // Then - 이벤트가 호출되었는지 검증
            mockedStaticEvents.verify(() ->
                    Events.raise(Mockito.any(SelfIntroductionOpenStatusChangeEvent.class)),
                Mockito.times(1)  // 정확히 1번 호출 여부 확인
            );
        }
    }
}
