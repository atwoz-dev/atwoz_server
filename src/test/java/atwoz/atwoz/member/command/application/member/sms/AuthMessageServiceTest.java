package atwoz.atwoz.member.command.application.member.sms;

import atwoz.atwoz.member.command.application.member.exception.CodeNotMatchException;
import atwoz.atwoz.member.command.infra.member.AuthMessageRedisRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthMessageServiceTest {

    @Mock
    AuthMessageRedisRepository authMessageRedisRepository;

    @InjectMocks
    AuthMessageService authMessageService;

    @DisplayName("해당 번호의 키가 존재하지 않는 경우, 예외 발생.")
    @Test
    void throwsExceptionWhenKeyNotExists() {
        // Given
        String phoneNumber = "01012345678";
        String code = "123456";

        Mockito.when(authMessageRedisRepository.getByKey(phoneNumber))
            .thenReturn(null);

        // When & Then
        Assertions.assertThatThrownBy(() -> authMessageService.authenticate(phoneNumber, code))
            .isInstanceOf(CodeNotMatchException.class);
    }

    @DisplayName("키가 일치하지 않는 경우, 예외 발생.")
    @Test
    void throwsExceptionWhenCodeNotEqualValue() {
        // Given
        String phoneNumber = "01012345678";
        String code = "123456";
        String value = "654321";

        Mockito.when(authMessageRedisRepository.getByKey(phoneNumber))
            .thenReturn(value);

        // When & Then
        Assertions.assertThatThrownBy(() -> authMessageService.authenticate(phoneNumber, code))
            .isInstanceOf(CodeNotMatchException.class);
    }
}
