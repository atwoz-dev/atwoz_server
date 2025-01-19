package atwoz.atwoz.hobby.domain;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.hobby.exception.InvalidHobbyNameException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "hobbies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hobby extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String name;

    public static Hobby from(String name) {
        return new Hobby(name);
    }

    private Hobby(@NonNull String name) {
        if (name.isBlank()) {
            throw new InvalidHobbyNameException();
        }
        this.name = name;
    }
}
