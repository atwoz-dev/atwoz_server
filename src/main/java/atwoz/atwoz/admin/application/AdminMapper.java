package atwoz.atwoz.admin.application;

import atwoz.atwoz.admin.application.dto.AdminSignUpRequest;
import atwoz.atwoz.admin.application.dto.AdminSignUpResponse;
import atwoz.atwoz.admin.domain.admin.Admin;
import atwoz.atwoz.admin.domain.admin.Password;
import atwoz.atwoz.common.domain.vo.Email;
import atwoz.atwoz.common.domain.vo.Name;
import atwoz.atwoz.common.domain.vo.PhoneNumber;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class AdminMapper {

    public static Admin toAdmin(AdminSignUpRequest request, Password password) {
        return Admin.builder()
                .email(Email.from(request.email()))
                .password(password)
                .name(Name.from(request.name()))
                .phoneNumber(PhoneNumber.from(request.phoneNumber()))
                .build();
    }

    public static AdminSignUpResponse toSignUpResponse(Admin admin) {
        return new AdminSignUpResponse(
                admin.getId(),
                admin.getEmail().getAddress(),
                admin.getName().getValue(),
                admin.getPhoneNumber().getValue()
        );
    }
}
