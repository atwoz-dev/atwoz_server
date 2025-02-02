package atwoz.atwoz.admin.command.domain.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminCommandRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(Email email);
}
