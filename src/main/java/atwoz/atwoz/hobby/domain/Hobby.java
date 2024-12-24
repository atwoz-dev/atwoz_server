package atwoz.atwoz.hobby.domain;

import atwoz.atwoz.common.domain.BaseEntity;
import atwoz.atwoz.hobby.exception.InvalidHobbyNameException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hobby extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Hobby(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    private void setName(@NonNull String name) {
        if (name.trim().isEmpty())
            throw new InvalidHobbyNameException();
        this.name = name;
    }

}
