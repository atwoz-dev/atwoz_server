package atwoz.atwoz.admin.application;

import atwoz.atwoz.admin.application.dto.AdminSignupRequest;
import atwoz.atwoz.admin.application.dto.AdminSignupResponse;
import atwoz.atwoz.admin.application.exception.DuplicateEmailException;
import atwoz.atwoz.admin.domain.admin.Admin;
import atwoz.atwoz.admin.domain.admin.Password;
import atwoz.atwoz.admin.domain.repository.AdminRepository;
import atwoz.atwoz.admin.domain.service.PasswordHasher;
import atwoz.atwoz.common.domain.vo.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordHasher passwordHasher;

    @Transactional
    public AdminSignupResponse signUp(AdminSignupRequest request) {
        validateEmailUniqueness(request);
        Admin newAdmin = createAdmin(request);
        return AdminMapper.toSignUpResponse(newAdmin);
    }

    private void validateEmailUniqueness(AdminSignupRequest request) {
        String email = request.email();
        adminRepository.findByEmail(Email.from(email))
                .ifPresent(admin -> { throw new DuplicateEmailException(email); });
    }

    private Admin createAdmin(AdminSignupRequest request) {
        Password password = Password.fromRaw(request.password(), passwordHasher);
        Admin newAdmin = AdminMapper.toAdmin(request, password);
        return adminRepository.save(newAdmin);
    }
}
