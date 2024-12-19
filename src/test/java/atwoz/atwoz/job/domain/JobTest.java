package atwoz.atwoz.job.domain;

import atwoz.atwoz.job.exception.InvalidJobNameException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JobTest {

    @Test
    @DisplayName("직업명이 null인 경우, 유효하지 않다.")
    public void invalidWhenJobNameIsNull() {
        // Given
        String name = null;

        // When & Then
        Assertions.assertThatThrownBy(() -> new Job(name))
                .isInstanceOf(InvalidJobNameException.class);
    }

    @Test
    @DisplayName("직업명이 null이 아닌 경우, 유효하다.")
    public void isValidWhenJobNameIsNotNull() {
        // Given
        String name = "JOB_NAME";

        // When
        Job job = new Job(name);

        // Then
        Assertions.assertThat(job.getName()).isEqualTo(name);
    }
}
