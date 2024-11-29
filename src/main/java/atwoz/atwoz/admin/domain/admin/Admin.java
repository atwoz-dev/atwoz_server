package atwoz.atwoz.admin.domain.admin;

import atwoz.atwoz.common.domain.SoftDeleteBaseEntity;
import atwoz.atwoz.common.domain.vo.Email;
import atwoz.atwoz.common.domain.vo.Name;
import atwoz.atwoz.common.domain.vo.PhoneNumber;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends SoftDeleteBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Email email;

    @Embedded
    private Password password;

    @Embedded
    private Name name;

    @Embedded
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

    private void setEmail(Email email) {
        if (email == null) throw new IllegalArgumentException("Email은 null일 수 없습니다.");
        this.email = email;
    }

    private void setPassword(Password password) {
        if (password == null) throw new IllegalArgumentException("Password는 null일 수 없습니다.");
        this.password = password;
    }

    private void setName(Name name) {
        if (name == null) throw new IllegalArgumentException("Name은 null일 수 없습니다.");
        this.name = name;
    }

    private void setPhoneNumber(PhoneNumber phoneNumber) {
        if (phoneNumber == null) throw new IllegalArgumentException("PhoneNumber는 null일 수 없습니다.");
        this.phoneNumber = phoneNumber;
    }
}