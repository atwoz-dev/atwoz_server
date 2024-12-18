package atwoz.atwoz.job.domain;

import atwoz.atwoz.common.domain.BaseEntity;
import atwoz.atwoz.job.exception.InvalidJobCodeException;
import atwoz.atwoz.job.exception.InvalidJobNameException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Job extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;

    @Builder
    private Job(String name, String code) {
        validateName(name);
        validateCode(code);
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    private static void validateName(String name) {
        if (name == null) {
            throw new InvalidJobNameException();
        }
    }

    private static void validateCode(String code) {
        if (code == null) {
            throw new InvalidJobCodeException();
        }
    }
}
