package atwoz.atwoz.job.domain;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.job.exception.InvalidJobNameException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Job extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Getter
    private String name;

    public static Job from(String name) {
        return new Job(name);
    }

    private Job(@NonNull String name) {
        if (name.isBlank()) {
            throw new InvalidJobNameException();
        }
        this.name = name;
    }
}
