package atwoz.atwoz.admin.domain.admin;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Email email;

    @Embedded
    private Password password;

    private String comment;

    @Enumerated(STRING)
    @Column(length = 50)
    private AdminRole role;

    @Enumerated(STRING)
    @Column(length = 50)
    private ApprovalStatus approvalStatus;

    @Builder
    private Admin(Email email, Password password, String comment) {
        if (email == null) {
            throw new IllegalArgumentException("Email은 null일 수 없습니다.");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password는 null일 수 없습니다.");
        }
        this.email = email;
        this.password = password;
        this.comment = comment;
        this.role = AdminRole.GENERAL;
        this.approvalStatus = ApprovalStatus.PENDING;
    }
}