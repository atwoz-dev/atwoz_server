package atwoz.atwoz.admin.domain.admin;

import atwoz.atwoz.common.domain.SoftDeleteBaseEntity;
import atwoz.atwoz.common.domain.vo.Email;
import atwoz.atwoz.common.domain.vo.Name;
import atwoz.atwoz.common.domain.vo.PhoneNumber;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.EnumType.STRING;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends SoftDeleteBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Embedded
    @Getter
    private Email email;

    @Embedded
    private Password password;

    @Embedded
    @Getter
    private Name name;

    @Embedded
    @Getter
    private PhoneNumber phoneNumber;

    private String comment;

    @Enumerated(STRING)
    @Column(columnDefinition = "varchar(50)")
    private AdminRole role;

    @Enumerated(STRING)
    @Column(columnDefinition = "varchar(50)")
    private ApprovalStatus approvalStatus;

    @Builder
    private Admin(Email email, Password password, Name name, PhoneNumber phoneNumber, String comment) {
        setEmail(email);
        setPassword(password);
        setName(name);
        setPhoneNumber(phoneNumber);
        this.comment = comment;
        this.role = AdminRole.GENERAL;
        this.approvalStatus = ApprovalStatus.PENDING;
    }

    private void setEmail(@NonNull Email email) {
        this.email = email;
    }

    private void setPassword(@NonNull Password password) {
        this.password = password;
    }

    private void setName(@NonNull Name name) {
        this.name = name;
    }

    private void setPhoneNumber(@NonNull PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHashedPassword() {
        return password.getHashedValue();
    }
}