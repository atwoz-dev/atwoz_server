package atwoz.atwoz.admin.domain.admin;

import atwoz.atwoz.common.domain.Email;
import atwoz.atwoz.common.domain.Name;
import atwoz.atwoz.common.domain.PhoneNumber;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin {
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
    @Column(length = 50)
    private AdminRole role;

    @Enumerated(STRING)
    @Column(length = 50)
    private ApprovalStatus approvalStatus;

    @Builder
    private Admin(Email email, Password password, Name name, PhoneNumber phoneNumber, String comment) {
        if (email == null) {
            throw new IllegalArgumentException("Email은 null일 수 없습니다.");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password는 null일 수 없습니다.");
        }
        if (name == null) {
            throw new IllegalArgumentException("Name은 null일 수 없습니다.");
        }
        if (phoneNumber == null) {
            throw new IllegalArgumentException("PhoneNumber는 null일 수 없습니다.");
        }
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.comment = comment;
        this.role = AdminRole.GENERAL;
        this.approvalStatus = ApprovalStatus.PENDING;
    }
}