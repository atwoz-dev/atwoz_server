package atwoz.atwoz.admin.domain.application;

import atwoz.atwoz.admin.domain.admin.Admin;
import atwoz.atwoz.admin.domain.admin.Password;
import atwoz.atwoz.admin.domain.application.dto.AdminSignUpRequest;
import atwoz.atwoz.admin.domain.application.dto.AdminSignUpResponse;
import atwoz.atwoz.admin.domain.application.exception.DuplicateAdminException;
import atwoz.atwoz.admin.domain.repository.AdminRepository;
import atwoz.atwoz.common.domain.vo.Email;
import atwoz.atwoz.common.domain.vo.Name;
import atwoz.atwoz.common.domain.vo.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminRepository adminRepository;

    @Transactional
    public AdminSignUpResponse signUp(AdminSignUpRequest request) {
        validateEmailUniqueness(request);
        Admin newAdmin = createAdmin(request);
        return adminToSignUpResponse(newAdmin);
    }

    private void validateEmailUniqueness(AdminSignUpRequest request) {
        adminRepository.findByEmail(Email.from(request.email()))
                .ifPresent(admin -> { throw new DuplicateAdminException("이미 사용 중인 이메일입니다."); });
    }

    private Admin createAdmin(AdminSignUpRequest request) {
        Admin newAdmin = Admin.builder()
                .email(Email.from(request.email()))
                .password(Password.from(request.password()))
                .name(Name.from(request.name()))
                .phoneNumber(PhoneNumber.from(request.phoneNumber()))
                .build();

        return adminRepository.save(newAdmin);
    }

    private Password encryptPassword(String password) {
        // TODO: password 암호화
        return null;
    }

    private AdminSignUpResponse adminToSignUpResponse(Admin admin) {
        return new AdminSignUpResponse(
                admin.getId(),
                admin.getEmail().getAddress(),
                admin.getName().getValue(),
                admin.getPhoneNumber().getValue()
        );
    }
}
