package atwoz.atwoz.job.domain;

import atwoz.atwoz.common.BaseEntity;
import atwoz.atwoz.job.exception.InvalidJobNameException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Job extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;

    public Job(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    private void setName(@NonNull String name) {
        if (name.isBlank())
            throw new InvalidJobNameException();
        this.name = name;
    }

}
