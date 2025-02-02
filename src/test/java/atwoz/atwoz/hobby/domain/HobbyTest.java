package atwoz.atwoz.hobby.domain;

import atwoz.atwoz.hobby.command.domain.Hobby;
import atwoz.atwoz.hobby.command.domain.InvalidHobbyNameException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HobbyTest {

    @Test
    @DisplayName("취미명이 null인 경우, 유효하지 않다.")
    void invalidWhenHobbyNameIsNull() {
        // Given
        String name = null;

        // When & Then
        Assertions.assertThatThrownBy(() -> Hobby.from(name))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("취미명이 단순 빈 문자열인 경우, 유효하지 않다.")
    void inValidWhenHobbyNameIsEmpty() {
        // Given
        String name = " ";

        // When & Then
        Assertions.assertThatThrownBy(() -> Hobby.from(name))
                .isInstanceOf(InvalidHobbyNameException.class);
    }

    @Test
    @DisplayName("취미명이 null이 아닌 경우, 유효하다.")
    void isValidWhenHobbyNameIsNotNull() {
        // Given
        String name = "HOBBY_NAME";

        // When
        Hobby hobby = Hobby.from(name);

        // Then
        Assertions.assertThat(hobby.getName()).isEqualTo(name);
    }
}
