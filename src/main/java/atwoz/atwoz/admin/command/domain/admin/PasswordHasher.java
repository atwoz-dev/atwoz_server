package atwoz.atwoz.admin.command.domain.admin;

public interface PasswordHasher {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}
