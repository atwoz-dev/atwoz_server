package atwoz.atwoz.admin.application;

import atwoz.atwoz.admin.application.dto.AdminLoginRequest;
import atwoz.atwoz.admin.application.dto.AdminLoginResponse;
import atwoz.atwoz.admin.application.dto.AdminSignupRequest;
import atwoz.atwoz.admin.application.dto.AdminSignupResponse;
import atwoz.atwoz.admin.application.exception.AdminNotFoundException;
import atwoz.atwoz.admin.application.exception.DuplicateEmailException;
import atwoz.atwoz.admin.application.exception.PasswordMismatchException;
import atwoz.atwoz.admin.domain.admin.Admin;
import atwoz.atwoz.admin.domain.admin.Password;
import atwoz.atwoz.admin.domain.repository.AdminRepository;
import atwoz.atwoz.admin.domain.service.PasswordHasher;
import atwoz.atwoz.common.auth.context.Role;
import atwoz.atwoz.common.auth.jwt.JwtProvider;
import atwoz.atwoz.common.domain.vo.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordHasher passwordHasher;
    private final JwtProvider jwtProvider;

    @Transactional
    public AdminSignupResponse signup(AdminSignupRequest request) {
        validateEmailUniqueness(request);
        Admin newAdmin = createAdmin(request);
        return AdminAuthMapper.toSignupResponse(newAdmin);
    }

    // TODO: refresh token redis 관련 로직 추가
    @Transactional(readOnly = true)
    public AdminLoginResponse login(AdminLoginRequest request) {
        Admin admin = findAdminWith(request.email());
        validatePassword(request.password(), admin.getHashedPassword());

        Instant now = Instant.now();
        String accessToken = createAccessToken(admin.getId(), now);
        String refreshToken = createRefreshToken(admin.getId(), now);
        return AdminAuthMapper.toLoginResponse(accessToken, refreshToken);
    }

    private void validateEmailUniqueness(AdminSignupRequest request) {
        String email = request.email();
        adminRepository.findByEmail(Email.from(email))
                .ifPresent(admin -> { throw new DuplicateEmailException(email); });
    }

    private Admin createAdmin(AdminSignupRequest request) {
        Password password = Password.fromRaw(request.password(), passwordHasher);
        Admin newAdmin = AdminAuthMapper.toAdmin(request, password);
        return adminRepository.save(newAdmin);
    }

    private Admin findAdminWith(String email) {
        return adminRepository.findByEmail(Email.from(email))
                .orElseThrow(AdminNotFoundException::new);
    }

    private void validatePassword(String requestPassword, String hashedPassword) {
        if (!passwordHasher.matches(requestPassword, hashedPassword)) {
            throw new PasswordMismatchException();
        }
    }

    private String createAccessToken(Long id, Instant now) {
        return jwtProvider.createAccessToken(id, Role.ADMIN, now);
    }

    private String createRefreshToken(Long id, Instant now) {
        return jwtProvider.createRefreshToken(id, Role.ADMIN, now);
    }
}
