package atwoz.atwoz.job.domain;

import atwoz.atwoz.job.exception.InvalidJobCodeException;
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
        String code = "JOB_CODE";


        // When & Then
        Assertions.assertThatThrownBy(() -> Job.builder()
                        .name(name)
                        .code(code)
                        .build())
                .isInstanceOf(InvalidJobNameException.class);
    }

    @Test
    @DisplayName("직업 코드가 null인 경우, 유효하지 않다.")
    public void invalidWhenJobCodeIsNull() {
        // Given
        String name = "JOB_NAME";
        String code = null;

        // When & Then
        Assertions.assertThatThrownBy(() -> Job.builder()
                        .name(name)
                        .code(code)
                        .build())
                .isInstanceOf(InvalidJobCodeException.class);
    }

    @Test
    @DisplayName("직업명과 코드가 null이 아닌 경우, 유효하다.")
    public void isValidWhenJobCodeAndJobNameAreNotNull() {
        // Given
        String name = "JOB_NAME";
        String code = "JOB_CODE";

        // When
        Job job = Job.builder()
                .name(name)
                .code(code)
                .build();

        // Then
        Assertions.assertThat(job.getName()).isEqualTo(name);
        Assertions.assertThat(job.getCode()).isEqualTo(code);
    }
}
