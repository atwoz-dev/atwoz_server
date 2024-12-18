package atwoz.atwoz.admin.application;

import atwoz.atwoz.admin.application.dto.AdminSignUpRequest;
import atwoz.atwoz.admin.application.dto.AdminSignUpResponse;
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

    private final PasswordHasher passwordHasher;
    private final AdminRepository adminRepository;

    @Transactional
    public AdminSignUpResponse signUp(AdminSignUpRequest request) {
        validateEmailUniqueness(request);
        Admin newAdmin = createAdmin(request);
        return AdminMapper.toSignUpResponse(newAdmin);
    }

    private void validateEmailUniqueness(AdminSignUpRequest request) {
        String email = request.email();
        adminRepository.findByEmail(Email.from(email))
                .ifPresent(admin -> { throw new DuplicateEmailException(email); });
    }

    private Admin createAdmin(AdminSignUpRequest request) {
        Password password = Password.fromRaw(request.password(), passwordHasher);
        Admin newAdmin = AdminMapper.toAdmin(request, password);
        return adminRepository.save(newAdmin);
    }
}
