package atwoz.atwoz.admin.application;

import atwoz.atwoz.admin.application.dto.AdminLoginResponse;
import atwoz.atwoz.admin.application.dto.AdminSignupRequest;
import atwoz.atwoz.admin.application.dto.AdminSignupResponse;
import atwoz.atwoz.admin.domain.admin.Admin;
import atwoz.atwoz.admin.domain.admin.Password;
import atwoz.atwoz.common.domain.vo.Email;
import atwoz.atwoz.common.domain.vo.Name;
import atwoz.atwoz.common.domain.vo.PhoneNumber;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class AdminAuthMapper {

    public static Admin toAdmin(AdminSignupRequest request, Password password) {
        return Admin.builder()
                .email(Email.from(request.email()))
                .password(password)
                .name(Name.from(request.name()))
                .phoneNumber(PhoneNumber.from(request.phoneNumber()))
                .build();
    }

    public static AdminSignupResponse toSignupResponse(Admin admin) {
        return new AdminSignupResponse(
                admin.getId(),
                admin.getEmail().getAddress(),
                admin.getName().getValue(),
                admin.getPhoneNumber().getValue()
        );
    }

    public static AdminLoginResponse toLoginResponse(String accessToken, String refreshToken) {
        return new AdminLoginResponse(accessToken, refreshToken);
    }
}
