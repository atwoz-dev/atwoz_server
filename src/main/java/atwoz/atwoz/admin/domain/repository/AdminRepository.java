package atwoz.atwoz.admin.domain.repository;

import atwoz.atwoz.admin.domain.admin.Admin;
import atwoz.atwoz.common.domain.vo.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(Email email);
}
