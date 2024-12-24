package atwoz.atwoz.hobby.domain;

import atwoz.atwoz.hobby.exception.InvalidHobbyNameException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HobbyTest {

    @Test
    @DisplayName("취미명이 null인 경우, 유효하지 않다.")
    public void invalidWhenHobbyNameIsNull() {
        // Given
        String name = null;

        // When & Then
        Assertions.assertThatThrownBy(() -> new Hobby(name))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("취미명이 단순 빈 문자열인 경우, 유효하지 않다.")
    public void inValidWhenHobbyNameIsEmpty() {
        // Given
        String name = " ";

        // When & Then
        Assertions.assertThatThrownBy(() -> new Hobby(name))
                .isInstanceOf(InvalidHobbyNameException.class);
    }

    @Test
    @DisplayName("취미명이 null이 아닌 경우, 유효하다.")
    public void isValidWhenHobbyNameIsNotNull() {
        // Given
        String name = "HOBBY_NAME";

        // When
        Hobby hobby = new Hobby(name);

        // Then
        Assertions.assertThat(hobby.getName()).isEqualTo(name);
    }
}
