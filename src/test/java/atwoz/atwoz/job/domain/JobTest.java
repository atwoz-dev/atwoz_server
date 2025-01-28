package atwoz.atwoz.job.domain;

import atwoz.atwoz.job.command.domain.Job;
import atwoz.atwoz.job.command.exception.InvalidJobNameException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JobTest {

    @Test
    @DisplayName("직업명이 null인 경우, 유효하지 않다.")
    void invalidWhenJobNameIsNull() {
        // Given
        String name = null;

        // When & Then
        Assertions.assertThatThrownBy(() -> Job.from(name))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("직업명이 단순 빈 문자열인 경우, 유효하지 않다.")
    void invalidWhenJobNameIsEmpty() {
        //Given
        String name = "";

        // When & Then
        Assertions.assertThatThrownBy(() -> Job.from(name))
                .isInstanceOf(InvalidJobNameException.class);
    }

    @Test
    @DisplayName("직업명이 null이 아닌 경우, 유효하다.")
    void isValidWhenJobNameIsNotNull() {
        // Given
        String name = "JOB_NAME";

        // When
        Job job = Job.from(name);

        // Then
        Assertions.assertThat(job.getName()).isEqualTo(name);
    }
}
