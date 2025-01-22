package atwoz.atwoz.admin.command.application.admin;

import atwoz.atwoz.admin.command.application.admin.dto.AdminLoginResponse;
import atwoz.atwoz.admin.command.application.admin.dto.AdminSignupRequest;
import atwoz.atwoz.admin.command.application.admin.dto.AdminSignupResponse;
import atwoz.atwoz.admin.command.domain.admin.*;
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
