package atwoz.atwoz.community.command.application.profileexchange;

import atwoz.atwoz.common.event.Events;
import atwoz.atwoz.community.command.application.profileexchange.exception.ProfileExchangeAlreadyExists;
import atwoz.atwoz.community.command.domain.profileexchange.ProfileExchange;
import atwoz.atwoz.community.command.domain.profileexchange.ProfileExchangeRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ProfileExchangeServiceTest {

    private static MockedStatic<Events> mockedEvents;

    @Mock
    private ProfileExchangeRepository profileExchangeRepository;

    @InjectMocks
    private ProfileExchangeService profileExchangeService;

    @BeforeEach
    void setUp() {
        mockedEvents = Mockito.mockStatic(Events.class);
        mockedEvents.when(() -> Events.raise(Mockito.any()))
            .thenAnswer(invocation -> null);
    }

    @AfterEach
    void tearDown() {
        mockedEvents.close();
    }

    @Nested
    @DisplayName("프로필 교환 요청")
    class Request {

        @Test
        @DisplayName("프로필 교환이 이미 존재할 경우, 예외 발생")
        void throwsExceptionWhenProfileExchangeExistsBetween() {
            // Given
            long requesterId = 1L;
            long responderId = 2L;

            Mockito.when(profileExchangeRepository.existsProfileExchangeBetween(requesterId, responderId))
                .thenReturn(true);

            Mockito.doAnswer(invocation -> {
                Runnable runnable = invocation.getArgument(1);
                runnable.run();
                return null;
            }).when(profileExchangeRepository).withNamedLock(any(), any());

            // When & Then
            assertThatThrownBy(() -> profileExchangeService.request(requesterId, responderId))
                .isInstanceOf(ProfileExchangeAlreadyExists.class);
        }

        @DisplayName("프로필 교환이 존재하지 않으면, 프로필 교환을 생성한다.")
        @Test
        void request() {
            // Given
            long requesterId = 1L;
            long responderId = 2L;

            Mockito.when(profileExchangeRepository.existsProfileExchangeBetween(requesterId, responderId))
                .thenReturn(false);

            Mockito.doAnswer(invocation -> {
                Runnable runnable = invocation.getArgument(1);
                runnable.run();
                return null;
            }).when(profileExchangeRepository).withNamedLock(any(), any());

            // When
            profileExchangeService.request(requesterId, responderId);

            // Then
            Mockito.verify(profileExchangeRepository).save(Mockito.any(ProfileExchange.class));
        }
    }

    @Nested
    @DisplayName("프로필 교환 응답")
    class Response {
        
    }
}
