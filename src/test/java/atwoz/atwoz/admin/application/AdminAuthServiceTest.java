package atwoz.atwoz.admin.application;

import atwoz.atwoz.admin.application.dto.AdminSignupRequest;
import atwoz.atwoz.admin.application.dto.AdminSignupResponse;
import atwoz.atwoz.admin.application.exception.DuplicateEmailException;
import atwoz.atwoz.admin.domain.admin.Admin;
import atwoz.atwoz.admin.domain.admin.InvalidPasswordException;
import atwoz.atwoz.admin.domain.repository.AdminRepository;
import atwoz.atwoz.admin.domain.service.PasswordHasher;
import atwoz.atwoz.common.domain.vo.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class AdminAuthServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private AdminAuthService adminAuthService;

    @Test
    @DisplayName("중복되지 않은 이메일과 유효한 비밀번호로 회원가입 할 수 있습니다.")
    void canSignUpWhenRequestIsValid() {
        // given
        String email = "test@example.com";
        String rawPassword = "password123^^";
        AdminSignupRequest request = new AdminSignupRequest(email, rawPassword, "홍길동", "01012345678");

        when(adminRepository.findByEmail(Email.from(email)))
                .thenReturn(Optional.empty());
        when(passwordHasher.hash(rawPassword))
                .thenReturn("hashed-password123^^");
        when(adminRepository.save(any(Admin.class)))
                .thenAnswer(invocation -> {
                    Admin admin = invocation.getArgument(0, Admin.class);
                    setField(admin, "id", 1L);
                    return admin;
                });

        // when
        AdminSignupResponse response = adminAuthService.signUp(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo(email);

        verify(adminRepository).findByEmail(Email.from(email));
        verify(passwordHasher).hash(rawPassword);
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입을 시도하면 DuplicateEmailException이 발생합니다.")
    void throwDuplicateEmailExceptionWhenEmailAlreadyExists() {
        // given
        String email = "exists@example.com";
        AdminSignupRequest request = new AdminSignupRequest(email, "password123^^", "홍길동", "01012345678");

        when(adminRepository.findByEmail(Email.from(email)))
                .thenReturn(Optional.of(mock(Admin.class)));

        // when & then
        assertThatThrownBy(() -> adminAuthService.signUp(request))
                .isInstanceOf(DuplicateEmailException.class);

        verify(adminRepository, never()).save(any(Admin.class));
        verify(passwordHasher, never()).hash(anyString());
    }

    @Test
    @DisplayName("비밀번호 규칙을 만족하지 못하면 InvalidPasswordException이 발생합니다.")
    void throwInvalidPasswordExceptionWhenPasswordIsInvalid() {
        // given
        String email = "test2@example.com";
        String invalidPassword = "short12^^";
        AdminSignupRequest request = new AdminSignupRequest(email, invalidPassword, "홍길동", "01012345678");

        when(adminRepository.findByEmail(Email.from(email)))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminAuthService.signUp(request))
                .isInstanceOf(InvalidPasswordException.class);

        verify(adminRepository).findByEmail(Email.from(email));
        verify(passwordHasher, never()).hash(anyString());
        verify(adminRepository, never()).save(any(Admin.class));
    }
}
