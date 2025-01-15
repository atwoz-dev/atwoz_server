package atwoz.atwoz.admin.application;

import atwoz.atwoz.admin.application.dto.AdminLoginRequest;
import atwoz.atwoz.admin.application.dto.AdminLoginResponse;
import atwoz.atwoz.admin.application.dto.AdminSignupRequest;
import atwoz.atwoz.admin.application.dto.AdminSignupResponse;
import atwoz.atwoz.admin.application.exception.AdminNotFoundException;
import atwoz.atwoz.admin.application.exception.DuplicateEmailException;
import atwoz.atwoz.admin.application.exception.PasswordMismatchException;
import atwoz.atwoz.admin.domain.*;
import atwoz.atwoz.auth.domain.TokenProvider;
import atwoz.atwoz.auth.domain.TokenRepository;
import atwoz.atwoz.common.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordHasher passwordHasher;
    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;

    @Transactional
    public AdminSignupResponse signup(AdminSignupRequest request) {
        validateEmailUniqueness(request.email());
        Admin newAdmin = createAdmin(request);
        return AdminAuthMapper.toSignupResponse(newAdmin);
    }

    @Transactional
    public AdminLoginResponse login(AdminLoginRequest request) {
        Admin admin = findAdminByEmail(request.email());
        matchPassword(request.password(), admin.getPassword());

        Instant issuedAt = Instant.now();
        String accessToken = createAccessToken(admin.getId(), issuedAt);
        String refreshToken = createRefreshToken(admin.getId(), issuedAt);
        tokenRepository.save(refreshToken);

        return AdminAuthMapper.toLoginResponse(accessToken, refreshToken);
    }

    public void logout(String refreshToken) {
        tokenRepository.delete(refreshToken);
    }

    private void validateEmailUniqueness(String email) {
        adminRepository.findByEmail(Email.from(email))
                .ifPresent(admin -> { throw new DuplicateEmailException(); });
    }

    private Admin createAdmin(AdminSignupRequest request) {
        Password password = Password.fromRaw(request.password(), passwordHasher);
        Admin newAdmin = AdminAuthMapper.toAdmin(request, password);
        return adminRepository.save(newAdmin);
    }

    private Admin findAdminByEmail(String email) {
        return adminRepository.findByEmail(Email.from(email))
                .orElseThrow(AdminNotFoundException::new);
    }

    private void matchPassword(String requestPassword, Password password) {
        if (!password.matches(requestPassword, passwordHasher)) {
            throw new PasswordMismatchException();
        }
    }

    private String createAccessToken(Long id, Instant issuedAt) {
        return tokenProvider.createAccessToken(id, Role.ADMIN, issuedAt);
    }

    private String createRefreshToken(Long id, Instant issuedAt) {
        return tokenProvider.createRefreshToken(id, Role.ADMIN, issuedAt);
    }
}
