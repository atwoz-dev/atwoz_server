package atwoz.atwoz.job.domain;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.job.exception.InvalidJobNameException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "jobs")
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
